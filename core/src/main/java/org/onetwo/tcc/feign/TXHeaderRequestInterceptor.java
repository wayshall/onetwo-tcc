package org.onetwo.tcc.feign;

import java.util.Set;

import org.onetwo.common.web.utils.WebHolder;
import org.onetwo.tcc.core.internal.DefaultRemoteTXContextLookupService;

import com.google.common.collect.Sets;

import feign.RequestInterceptor;
import feign.RequestTemplate;
import lombok.extern.slf4j.Slf4j;

/**
 * @author wayshall
 * <br/>
 */
@Slf4j
public class TXHeaderRequestInterceptor implements RequestInterceptor {
	private Set<String> txHeaders = Sets.newHashSet(DefaultRemoteTXContextLookupService.HEADER_GTXID, 
													DefaultRemoteTXContextLookupService.HEADER_PTXID);
	@Override
	public void apply(RequestTemplate template) {
		WebHolder.getRequest().ifPresent(request -> {
			txHeaders.stream().forEach(header -> {
				String value = request.getHeader(header);
				template.header(header, value);
				if(log.isDebugEnabled()){
					log.debug("set tcc header[{} : {}] to feign request...", header, value);
				}
			});
		});
	}

	
}
