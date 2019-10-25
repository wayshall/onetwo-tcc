package org.onetwo.tcc;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.onetwo.common.utils.LangUtils;
import org.onetwo.common.utils.StringUtils;
import org.onetwo.dbm.id.SnowflakeIdGenerator;
import org.onetwo.tcc.annotation.TCCTransactional;
import org.onetwo.tcc.exception.TCCErrors;
import org.onetwo.tcc.exception.TCCException;
import org.onetwo.tcc.exception.TCCRemoteException;
import org.onetwo.tcc.spi.GlobalTransactionIdLookupService;
import org.onetwo.tcc.spi.TCCMessagePublisher;
import org.onetwo.tcc.spi.TXLogRepository;
import org.onetwo.tcc.util.TCCTransactionType;
import org.springframework.aop.support.AopUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import com.google.common.collect.Lists;

import lombok.Getter;

/**
 * @author weishao zeng
 * <br/>
 */
@Aspect
public class TransactionAspect {
//	private static final NamedThreadLocal<TransactionContext> CURRENT_CONTEXTS = new NamedThreadLocal<>("dtx-transaction");

	public static TransactionResourceHolder getCurrent(TransactionAspect key) {
		return (TransactionResourceHolder)TransactionSynchronizationManager.getResource(key);
	}
	
	@Autowired
	private GlobalTransactionIdLookupService globalTransactionIdLookupService;
	@Autowired
	@Getter
	private TXLogRepository txLogRepository;
	/*@Autowired
	private TXProcessor txProcessor;*/
	
    private SnowflakeIdGenerator idGenerator = new SnowflakeIdGenerator(31);
	
	private List<String> remoteExceptions = Lists.newArrayList("org.springframework.web.client.ResourceAccessException");
	
	@Around("org.onetwo.tcc.TCCTransactionPointcut.tccTransactional()")
	public Object startTransaction(ProceedingJoinPoint pjp) throws Throwable {
		MethodSignature ms = (MethodSignature)pjp.getSignature();
//		TransactionContext ctx = CURRENT_CONTEXTS.get();
		TransactionResourceHolder ctx = (TransactionResourceHolder)TransactionSynchronizationManager.getResource(this);
		TCCTransactional tccTransaction = null;
		if (ctx==null) {
			Optional<String> gtid = globalTransactionIdLookupService.findCurrentGTID();
			ctx = new TransactionResourceHolder(this);
			tccTransaction = AnnotationUtils.findAnnotation(ms.getMethod(), TCCTransactional.class);
			ctx.setConfirmMethod(tccTransaction.confirmMethod());
			ctx.setCancelMethod(tccTransaction.cancelMethod());
			ctx.setTarget(pjp.getTarget());
			ctx.setMethodArgs(pjp.getArgs());
			Class<?> targetClass = AopUtils.getTargetClass(pjp.getTarget());
			ctx.setTargetClass(targetClass);
			ctx.setSynchronizedWithTransaction(true);
			if (gtid.isPresent()) {
				ctx.setTransactionType(TCCTransactionType.BRANCH);
				ctx.setGtid(gtid.get());
				ctx.setCurrentTid("B" + nextId());
			} else {
				ctx.setTransactionType(TCCTransactionType.GLOBAL);
				ctx.setGtid("G" + nextId());
				ctx.setCurrentTid(ctx.getGtid());
			}
			ctx.check();
			
			if (StringUtils.isBlank(ctx.getConfirmMethod())) {
				throw new TCCException("the confirmMethod of @" + TCCTransactional.class.getSimpleName() + " can not be blank!");
			}
			if (StringUtils.isBlank(ctx.getCancelMethod())) {
				throw new TCCException("the cancelMethod of @" + TCCTransactional.class.getSimpleName() + " can not be blank!");
			}
			
			TransactionSynchronizationManager.bindResource(this, ctx);
			TCCTransactionSynchronization synchronization = new TCCTransactionSynchronization(ctx);
			TransactionSynchronizationManager.registerSynchronization(synchronization);

			ctx.createTxLog();
		} else {
			throw new TCCException(TCCErrors.ERR_ONLYONE_TCC_TRANSACTIONAL);
		}
		
		Object result = null;;
		try {
			result = pjp.proceed();
		} catch (Throwable e) {
			handleException(e);
		}
		
		return result;
	}
	
	protected void handleException(Throwable e) throws Throwable {
		if (isRemoteError(e)) {
			throw new TCCRemoteException(TCCErrors.ERR_REMOTE, e);
		} else {
			throw e;
		}
	}
	
	/***
	 * 是否远程调用异常
	 * @author weishao zeng
	 * @param t
	 * @return
	 */
	protected boolean isRemoteError(Throwable t) {
		if (t instanceof IOException) {
			return true;
		}
		Throwable rootCause = LangUtils.getFinalCauseException(t);
		if (rootCause instanceof IOException) {
			return true;
		}
		if (remoteExceptions.contains(t.getClass().getName())) {
			return true;
		}
		return false;
	}
	
	protected String nextId() {
		return String.valueOf(idGenerator.nextId());
	}
	
}

