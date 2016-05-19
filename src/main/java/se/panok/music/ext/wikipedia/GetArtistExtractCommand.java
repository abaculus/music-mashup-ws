package se.panok.music.ext.wikipedia;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import se.panok.music.utils.LogMessage;

import com.netflix.hystrix.HystrixCommand;
import com.netflix.hystrix.HystrixCommandGroupKey;
import com.netflix.hystrix.HystrixCommandKey;
import com.netflix.hystrix.HystrixThreadPoolKey;

public class GetArtistExtractCommand extends HystrixCommand<String> {

	private static final String GROUP_KEY = "GetArtistExtractGroup";

	private static final String COMMAND_KEY = "GetArtistExtractCommand";

	private static final String THREAD_POOL_KEY = "GetArtistExtractThreadPool";

	private final Logger logger = LoggerFactory.getLogger(getClass());

	private final WikipediaClient wikipediaClient;

	private final String artistId;

	public GetArtistExtractCommand(final WikipediaClient wikipediaClient,
			final String artistId) {
		super(Setter
				.withGroupKey(HystrixCommandGroupKey.Factory.asKey(GROUP_KEY))
				.andCommandKey(HystrixCommandKey.Factory.asKey(COMMAND_KEY))
				.andThreadPoolKey(
						HystrixThreadPoolKey.Factory.asKey(THREAD_POOL_KEY)));
		if (null == wikipediaClient) {
			throw new IllegalStateException("Client cannot be null!");
		}
		this.wikipediaClient = wikipediaClient;
		this.artistId = artistId;
	}

	@Override
	protected String run() throws Exception {
		return wikipediaClient.getArtistExtract(artistId);
	}

	@Override
	protected String getFallback() {
		logger.warn(
				LogMessage
						.createForAction("getFallback")
						.addPart("artistId", artistId)
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
