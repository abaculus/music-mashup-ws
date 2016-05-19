package se.panok.music.web;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import se.panok.music.service.ArtistService;
import se.panok.music.utils.LogMessage;
import se.panok.music.web.dto.Artist;

@RestController
@RequestMapping("/music/v1")
public class MusicController {

	private final Logger logger = LoggerFactory.getLogger(getClass());

	@Autowired
	private ArtistService artistService;

	@RequestMapping(value = "/artist/{mbid}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public Artist getBlockingArtist(@PathVariable("mbid") final String mbid) {

		logger.info(LogMessage.createForAction("getArtist")
				.addPart("mbid", mbid).toString());

		final Artist artist = Artist.convert(artistService.getArtistInfo(mbid));

		logger.info(LogMessage.createForAction("getArtist")
				.addPart("artist", artist).toString());

		return artist;
	}

	@ExceptionHandler(Exception.class)
	@ResponseStatus(value = HttpStatus.INTERNAL_SERVER_ERROR)
	public CustomError handleError(HttpServletRequest req, Exception exception) {
		logger.error(LogMessage.createForAction("handleError")
				.addPart("message", "Unexpected exception!")
				.addPart("requestUrl", req.getRequestURL()).toString());
		return new CustomError("Oups, things went wrong! Try again later.", 42);
	}

	private static class CustomError {

		private final String description;

		private final int errorCode;

		private CustomError(final String description, final int errorCode) {
			this.description = description;
			this.errorCode = errorCode;
		}

		public String getDescription() {
			return description;
		}

		public int getErrorCode() {
			return errorCode;
		}
	}
}
