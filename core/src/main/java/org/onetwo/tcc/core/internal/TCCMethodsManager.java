package org.onetwo.tcc.core.internal;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collector;

import org.onetwo.common.spring.SpringUtils;
import org.onetwo.tcc.core.annotation.TCCService;
import org.onetwo.tcc.core.annotation.TCCTransactional;
import org.onetwo.tcc.core.exception.TCCException;
import org.onetwo.tcc.core.util.TCCTransactionalMeta;
import org.onetwo.tcc.core.util.TCCUtils;
import org.springframework.aop.support.AopUtils;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.core.MethodIntrospector;
import org.springframework.core.MethodIntrospector.MetadataLookup;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.util.ReflectionUtils.MethodFilter;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;


/**
 * @author weishao zeng
 * <br/>
 */
//@Slf4j
public class TCCMethodsManager implements /*BeanPostProcessor,*/ InitializingBean {

	@Autowired
	private ApplicationContext applicationContext;
	private Cache<Method, TCCTransactionalMeta> caches = CacheBuilder.newBuilder()
																	.build();
	/*@Override
	public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
		return bean;
	}

	@Override
	public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
		checkTccService(bean);
		return bean;
	}*/


	@Override
	public void afterPropertiesSet() throws Exception {
		Map<Method, TCCTransactionalMeta> metas = SpringUtils.getBeansWithAnnotation(applicationContext, TCCService.class)
					.parallelStream()
					.map(d -> findTccMethodMetas(d.getBean()))
					.collect(Collector.of(
							() -> new HashMap<Method, TCCTransactionalMeta>(),
							(map, e) -> map.putAll(e),
							(left, right) -> {
								left.putAll(right);
								return left;
							}
							));
		caches.putAll(metas);
		/*if (log.isInfoEnabled()) {
			log.info("find tcc methods: {}", metas.keySet());
		}*/
	}
	
	public TCCTransactionalMeta getMeta(Class<?> targetType, Method method) {
		try {
			return caches.get(method, () -> {
				Optional<TCCTransactionalMeta> meta = TCCUtils.findTCCTransactionalMeta(targetType, method);
				if (!meta.isPresent()) {
					throw new TCCException("@" + TCCTransactional.class+" not found on method: " + method);
				}
				return meta.get();
			});
		} catch (ExecutionException e) {
			throw new TCCException("@" + TCCTransactional.class+" not found on method: " + method);
		}
	}
	
	protected Map<Method, TCCTransactionalMeta> findTccMethodMetas(Object bean) {
		Class<?> targetType = AopUtils.getTargetClass(bean);
		Map<Method, TCCTransactionalMeta> tccMethodMetas = MethodIntrospector.selectMethods(targetType, (MetadataLookup<TCCTransactionalMeta>)method -> {
			Optional<TCCTransactionalMeta> meta = TCCUtils.findTCCTransactionalMeta(targetType, method);
			if (meta.isPresent()) {
				TCCUtils.checkTccMethods(meta.get(), meta.get().isGlobalized());
				return meta.get();
			} else {
				return null;
			}
		});
		return tccMethodMetas;
	}
	
	protected void checkTccService(Object bean) {
		Class<?> targetType = AopUtils.getTargetClass(bean);
		MethodIntrospector.selectMethods(targetType, (MethodFilter)method-> {
			TCCTransactional tcc = AnnotationUtils.findAnnotation(method, TCCTransactional.class);
			return tcc!=null;
		}).stream().map(method -> {
			return TCCUtils.findTCCTransactionalMeta(targetType, method);
		}).forEach(meta -> {
			if (meta.isPresent()) {
				TCCUtils.checkTccMethods(meta.get(), meta.get().isGlobalized());
			}
		});;
	}

}

