package org.onetwo.tcc.feign;

import org.onetwo.tcc.core.internal.DefaultRemoteTXContextLookupService;
import org.onetwo.tcc.core.internal.TransactionResourceHolder;
import org.onetwo.tcc.core.util.TCCInvokeContext;

import feign.RequestInterceptor;
import feign.RequestTemplate;
import lombok.extern.slf4j.Slf4j;

/**
 * @author wayshall
 * <br/>
 */
@Slf4j
public class TXHeaderRequestInterceptor implements RequestInterceptor {
	/*private Set<String> txHeaders = Sets.newHashSet(DefaultRemoteTXContextLookupService.HEADER_GTXID, 
													DefaultRemoteTXContextLookupService.HEADER_PTXID);*/
	@Override
	public void apply(RequestTemplate template) {
		/*ServletRequestAttributes attrs = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
		if (attrs==null) {
			throw new TCCException(TCCErrors.ERR_WEB_REQUEST_NOT_FOUND);
		}*/
//		HttpServletRequest request = attrs.getRequest();
		TransactionResourceHolder resource = TCCInvokeContext.get();
		if (resource!=null) {
			template.header(DefaultRemoteTXContextLookupService.HEADER_GTXID, resource.getGtxId());
			template.header(DefaultRemoteTXContextLookupService.HEADER_PTXID, resource.getCurrentTxid());
			if(log.isDebugEnabled()){
				log.debug("set tcc header feign request, gid:{}, pid:{}", resource.getGtxId(), resource.getCurrentTxid());
			}
		}
	}

	
}
