package org.onetwo.tcc.resttemplate;

import java.io.IOException;

import org.onetwo.tcc.core.internal.DefaultRemoteTXContextLookupService;
import org.onetwo.tcc.core.internal.TransactionResourceHolder;
import org.onetwo.tcc.core.util.TCCInvokeContext;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;

import lombok.extern.slf4j.Slf4j;

/**
 * @author weishao zeng
 * <br/>
 */
@Slf4j
public class TXClientHttpRequestInterceptor implements ClientHttpRequestInterceptor {

	@Override
	public ClientHttpResponse intercept(HttpRequest request, byte[] body, ClientHttpRequestExecution execution) throws IOException {
		TransactionResourceHolder txHolder = TCCInvokeContext.get();
		if (txHolder!=null) {
			request.getHeaders().set(DefaultRemoteTXContextLookupService.HEADER_GTXID, txHolder.getGtxId());
			request.getHeaders().set(DefaultRemoteTXContextLookupService.HEADER_PTXID, txHolder.getCurrentTxid());
			if(log.isDebugEnabled()){
				log.debug("set tcc header spring resttemplate request, gid:{}, pid:{}", txHolder.getGtxId(), txHolder.getCurrentTxid());
			}
		}
		return execution.execute(request, body);
	}
	
	

}

