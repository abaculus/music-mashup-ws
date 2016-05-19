package se.panok.music.ext.coverart;

import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.Executors;

import org.apache.commons.collections.CollectionUtils;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import com.google.common.collect.Lists;
import com.netflix.config.ConfigurationManager;

public class GetCoverArtCommandTest {

	@Test
	public void saturatedThreadPoolTest() throws InterruptedException {
		
		final String MOCKED_COMMAND_ANSWER = "42";

		final CoverArtClient coverArtClientMock = Mockito
				.mock(CoverArtClient.class);
		Mockito.when(coverArtClientMock.getCoverArtUrl(Mockito.anyString()))
				.thenAnswer(new Answer<String>() {

					@Override
					public String answer(InvocationOnMock invocation)
							throws Throwable {
						Thread.sleep((long) (Math.random() * 500));
						return MOCKED_COMMAND_ANSWER;
					}

				});

		// only allow for 3 concurrent threads
		ConfigurationManager.getConfigInstance().setProperty(
				"hystrix.threadpool.default.coreSize", 3);

		// create commands
		final List<GetCoverArtUrlCommand> commands = createGetCoverArtCommand(
				coverArtClientMock, 4);
		final List<String> resultList = Lists.newArrayList();

		// create threads
		final List<Callable<Void>> threads = createCallable(commands,
				resultList);

		// simulate 4 threads calling
		Executors.newFixedThreadPool(4).invokeAll(threads);

		// 4 threads = 4 results
		Assert.assertEquals(4, resultList.size());

		List<String> successResults = Lists.newArrayList(resultList);
		List<String> fallbackResults = Lists.newArrayList(resultList);

		// 3 successful results
		CollectionUtils.filter(successResults,
				new org.apache.commons.collections.Predicate() {
					public boolean evaluate(Object obj) {
						return MOCKED_COMMAND_ANSWER.equals(obj);
					}
				});
		Assert.assertEquals(3, successResults.size());

		// 1 fallback result
		CollectionUtils.filter(fallbackResults,
				new org.apache.commons.collections.Predicate() {
					public boolean evaluate(Object obj) {
						return null == obj;
					}
				});

		Assert.assertEquals(1, fallbackResults.size());

		// 1 command should have been rejected due to pool saturation
		CollectionUtils.filter(commands,
				new org.apache.commons.collections.Predicate() {
					public boolean evaluate(Object obj) {
						return ((GetCoverArtUrlCommand) obj)
								.isResponseRejected();
					}
				});

		Assert.assertEquals(1, commands.size());
	}

	private List<GetCoverArtUrlCommand> createGetCoverArtCommand(
			final CoverArtClient coverArtClientMock, final int count) {
		List<GetCoverArtUrlCommand> commands = Lists.newArrayList();
		for (int i = 0; i < count; i++) {
			commands.add(new GetCoverArtUrlCommand(coverArtClientMock, ""));
		}
		return commands;
	}

	private List<Callable<Void>> createCallable(
			final List<GetCoverArtUrlCommand> commands,
			final List<String> resultList) {
		final List<Callable<Void>> callables = Lists.newArrayList();
		for (final GetCoverArtUrlCommand command : commands) {
			callables.add(new Callable<Void>() {

				@Override
				public Void call() throws Exception {
					final String result = command.execute();
					resultList.add(result);
					return null;
				}
			});
		}
		return callables;
	}
}
