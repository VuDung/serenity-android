/**
 * The MIT License (MIT)
 * Copyright (c) 2012 David Carver
 * Permission is hereby granted, free of charge, to any person obtaining
 * a copy of this software and associated documentation files (the
 * "Software"), to deal in the Software without restriction, including
 * without limitation the rights to use, copy, modify, merge, publish,
 * distribute, sublicense, and/or sell copies of the Software, and to
 * permit persons to whom the Software is furnished to do so, subject to
 * the following conditions:
 *
 * The above copyright notice and this permission notice shall be included
 * in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS
 * OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS
 * OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY,
 * WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF
 * OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package us.nineworlds.serenity.ui.util;

import java.util.LinkedList;

import javax.inject.Inject;

import us.nineworlds.serenity.R;
import us.nineworlds.serenity.core.SerenityConstants;
import us.nineworlds.serenity.core.externalplayer.ExternalPlayer;
import us.nineworlds.serenity.core.externalplayer.ExternalPlayerFactory;
import us.nineworlds.serenity.core.model.VideoContentInfo;
import us.nineworlds.serenity.core.util.TimeUtil;
import us.nineworlds.serenity.injection.BaseInjector;
import us.nineworlds.serenity.injection.ForVideoQueue;
import us.nineworlds.serenity.ui.video.player.SerenitySurfaceViewVideoActivity;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.widget.Toast;

public class VideoPlayerIntentUtils extends BaseInjector {

	@Inject
	@ForVideoQueue
	protected LinkedList<VideoContentInfo> videoQueue;

	@Inject
	protected SharedPreferences prefs;

	/**
	 * This must run on a UI thread.
	 *
	 * Launches an external player based on the information provided.
	 *
	 * @param videoContent
	 * @param activity
	 * @param autoResume
	 */
	public void launchExternalPlayer(VideoContentInfo videoContent,
			Activity activity, boolean autoResume) {

		String externalPlayerValue = prefs.getString(
				"serenity_external_player_filter", "default");

		if (!videoQueue.isEmpty() || "default".equals(externalPlayerValue)) {
			videoContent.setResumeOffset(0);
			launchPlayer(videoContent, activity);
			return;
		}

		if (videoContent.isPartiallyWatched() && !autoResume) {
			showResumeDialogQueue(activity, videoContent);
			return;
		}

		launchPlayer(videoContent, activity);
	}

	protected void launchPlayer(VideoContentInfo videoContent, Activity activity) {
		String externalPlayerValue = prefs.getString(
				"serenity_external_player_filter", "default");

		ExternalPlayerFactory factory = new ExternalPlayerFactory(videoContent,
				activity);
		ExternalPlayer extplay = factory
				.createExternalPlayer(externalPlayerValue);
		try {
			extplay.launch();
		} catch (ActivityNotFoundException ex) {
			extplay = factory.createExternalPlayer("default");
			extplay.launch();
		}
	}

	/**
	 * Play all videos in the queue launching the appropriate player.
	 *
	 * @param context
	 */
	public void playAllFromQueue(Activity context) {
		if (!videoQueue.isEmpty()) {
			boolean extplayer = prefs.getBoolean("external_player", false);
			boolean extplayerVideoQueue = prefs.getBoolean(
					"external_player_continuous_playback", false);

			if (extplayer) {
				if (extplayerVideoQueue) {
					VideoContentInfo videoContent = videoQueue.poll();
					launchExternalPlayer(videoContent, context, false);
				} else {
					Toast.makeText(
							context,
							context.getResources()
							.getString(
									R.string.external_player_video_queue_support_has_not_been_enabled_),
									Toast.LENGTH_LONG).show();
				}
			} else {
				Intent vpIntent = new Intent(context,
						SerenitySurfaceViewVideoActivity.class);
				context.startActivityForResult(vpIntent,
						SerenityConstants.EXIT_PLAYBACK_IMMEDIATELY);
			}
		} else {
			Toast.makeText(context,
					context.getResources().getString(R.string.queue_is_empty_),
					Toast.LENGTH_LONG).show();
		}
	}

	protected void showResumeDialogQueue(Activity context,
			VideoContentInfo videoContent) {
		final VideoContentInfo video = videoContent;
		final Activity c = context;
		AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
				context, android.R.style.Theme_Holo_Dialog);

		alertDialogBuilder.setTitle(R.string.resume_video);
		alertDialogBuilder
				.setMessage(
				context.getResources().getText(
						R.string.resume_the_video_from_)
						+ TimeUtil.formatDuration(video
								.getResumeOffset())
								+ context.getResources().getText(
										R.string._or_restart_))
				.setCancelable(false)
				.setPositiveButton(R.string.resume,
						new DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								launchPlayer(video, c);
							}
						})
				.setNegativeButton(R.string.restart,
						new DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								video.setResumeOffset(0);
								launchPlayer(video, c);
							}
						});

		alertDialogBuilder.create();
		AlertDialog dialog = alertDialogBuilder.show();
		dialog.getButton(DialogInterface.BUTTON_POSITIVE)
		.requestFocusFromTouch();
	}

	public void playVideo(Activity activity, VideoContentInfo videoInfo,
			boolean autoResume) {
		if (!videoQueue.isEmpty()) {
			Toast.makeText(activity, "Cleared video queue before playback.",
					Toast.LENGTH_LONG).show();
			videoQueue.clear();
		}

		boolean externalPlayer = prefs.getBoolean("external_player", false);

		if (externalPlayer) {
			launchExternalPlayer(videoInfo, activity, autoResume);
			return;
		}

		launchInternalPlayer(videoInfo, activity, autoResume);
	}

	/**
	 *
	 * @param videoInfo
	 * @return
	 * @param autoResume
	 */
	private void launchInternalPlayer(VideoContentInfo videoInfo,
			Activity activity, boolean autoResume) {

		videoQueue.add(videoInfo);

		Intent vpIntent = new Intent(activity,
				SerenitySurfaceViewVideoActivity.class);
		vpIntent.putExtra("autoResume", autoResume);

		activity.startActivityForResult(vpIntent,
				SerenityConstants.BROWSER_RESULT_CODE);
	}
}
