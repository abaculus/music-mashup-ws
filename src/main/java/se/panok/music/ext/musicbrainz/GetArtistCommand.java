package se.panok.music.ext.musicbrainz;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import se.panok.music.model.Artist;
import se.panok.music.utils.LogMessage;

import com.netflix.hystrix.HystrixCommand;
import com.netflix.hystrix.HystrixCommandGroupKey;
import com.netflix.hystrix.HystrixCommandKey;
import com.netflix.hystrix.HystrixThreadPoolKey;

public class GetArtistCommand extends HystrixCommand<Artist> {

	private static final String GROUP_KEY = "GetArtistGroup";

	private static final String COMMAND_KEY = "GetArtistCommand";

	private static final String THREAD_POOL_KEY = "GetArtistThreadPool";

	private final Logger logger = LoggerFactory.getLogger(getClass());

	private final MusicBrainzClient musicBrainzClient;

	private final String mbid;

	public GetArtistCommand(final MusicBrainzClient musicBrainzClient,
			final String mbid) {
		super(Setter
				.withGroupKey(HystrixCommandGroupKey.Factory.asKey(GROUP_KEY))
				.andCommandKey(HystrixCommandKey.Factory.asKey(COMMAND_KEY))
				.andThreadPoolKey(
						HystrixThreadPoolKey.Factory.asKey(THREAD_POOL_KEY)));
		if (null == musicBrainzClient) {
			throw new IllegalStateException("Client cannot be null!");
		}
		this.musicBrainzClient = musicBrainzClient;

		if (StringUtils.isBlank(mbid)) {
			throw new IllegalArgumentException("Blank 'mbid' is not allowed!");
		}
		this.mbid = mbid;
	}

	@Override
	protected Artist run() throws Exception {
		return musicBrainzClient.getArtist(mbid);
	}

	@Override
	protected Artist getFallback() {
		logger.warn(
				LogMessage
						.createForAction("getFallback")
						.addPart("mbid", mbid)
						.addPart("message",
								"Fallback triggered, returning empty artist.")
						.addPart("isResponseRejected", isResponseRejected())
						.addPart("isFailedExecution", isFailedExecution())
						.addPart("isResponseShortCircuited",
								isResponseShortCircuited())
						.addPart("isResponseTimedOut", isResponseTimedOut())
						.toString(), getFailedExecutionException());
		return new Artist();
	}
}
