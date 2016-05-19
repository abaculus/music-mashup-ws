package se.panok.music.ext.coverart;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import se.panok.music.utils.LogMessage;

import com.netflix.hystrix.HystrixCommand;
import com.netflix.hystrix.HystrixCommandGroupKey;
import com.netflix.hystrix.HystrixCommandKey;
import com.netflix.hystrix.HystrixThreadPoolKey;

public class GetCoverArtUrlCommand extends HystrixCommand<String> {

	private static final String GROUP_KEY = "GetCoverArtUrlGroup";

	private static final String COMMAND_KEY = "GetCoverArtUrlCommand";

	private static final String THREAD_POOL_KEY = "GetCoverArtUrlThreadPool";

	private final Logger logger = LoggerFactory.getLogger(getClass());

	private final CoverArtClient coverArtClient;

	private final String mbid;

	public GetCoverArtUrlCommand(final CoverArtClient coverArtClient,
			final String mbid) {
		super(Setter
				.withGroupKey(HystrixCommandGroupKey.Factory.asKey(GROUP_KEY))
				.andCommandKey(HystrixCommandKey.Factory.asKey(COMMAND_KEY))
				.andThreadPoolKey(
						HystrixThreadPoolKey.Factory.asKey(THREAD_POOL_KEY)));
		if (null == coverArtClient) {
			throw new IllegalStateException("Client cannot be null!");
		}
		this.coverArtClient = coverArtClient;
		this.mbid = mbid;
	}

	@Override
	protected String run() throws Exception {
		return coverArtClient.getCoverArtUrl(mbid);
	}

	@Override
	protected String getFallback() {
		logger.warn(
				LogMessage
						.createForAction("getFallback")
						.addPart("mbid", mbid)
						.addPart("message",
								"Fallback triggered, returning null.")
						.addPart("isResponseRejected", isResponseRejected())
						.addPart("isFailedExecution", isFailedExecution())
						.addPart("isResponseShortCircuited",
								isResponseShortCircuited())
						.addPart("isResponseTimedOut", isResponseTimedOut())
						.toString(), getFailedExecutionException());
		return null;
	}

}
