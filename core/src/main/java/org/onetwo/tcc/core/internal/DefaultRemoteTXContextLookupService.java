package org.onetwo.tcc.core.internal;

import java.util.Optional;

import org.onetwo.common.web.utils.WebHolder;
import org.onetwo.tcc.core.spi.TCCTXContextLookupService;

/**
 * @author weishao zeng
 * <br/>
 */
public class DefaultRemoteTXContextLookupService implements TCCTXContextLookupService {
	public static String HEADER_GTXID = "X-JFISH-TCC-GTXID";
	public static String HEADER_PTXID = "X-JFISH-TCC-PTXID";

	@Override
	public Optional<TXContext> findCurrent() {
		return WebHolder.getSpringContextHolderRequest().map(request -> {
			String gid = request.getHeader(HEADER_GTXID);
			String pid = request.getHeader(HEADER_PTXID);
			TXContext ctx = new TXContext();
			ctx.setGtxId(gid);
			ctx.setParentTxId(pid);
			return ctx;
		});
	}

}

