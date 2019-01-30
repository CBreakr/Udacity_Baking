package app.com.example.android.bakingtime;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.exoplayer2.DefaultRenderersFactory;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.PlaybackParameters;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.ui.SimpleExoPlayerView;

import app.com.example.android.bakingtime.RecipeUtils.RawJsonReader;
import app.com.example.android.bakingtime.RecipeUtils.Recipe;
import app.com.example.android.bakingtime.RecipeUtils.Step;
import app.com.example.android.bakingtime.UI_Utils.StateManagement;
import app.com.example.android.bakingtime.UI_Utils.SubState;

import butterknife.BindView;
import butterknife.ButterKnife;

import android.support.v4.media.session.MediaButtonReceiver;
import android.support.v4.media.session.MediaSessionCompat;
import android.support.v4.media.session.PlaybackStateCompat;

import com.google.android.exoplayer2.DefaultLoadControl;
import com.google.android.exoplayer2.ExoPlaybackException;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.LoadControl;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.Timeline;
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.TrackGroupArray;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelectionArray;
import com.google.android.exoplayer2.trackselection.TrackSelector;
import com.google.android.exoplayer2.ui.PlayerView;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.util.Util;
import com.google.android.exoplayer2.video.VideoRendererEventListener;

public class IndividualStepFragment extends StateParameterFragment
    implements View.OnClickListener, ExoPlayer.EventListener
{
    private boolean mIsVertical = true;
    private Step mStep;
    private long mCurrentVideoPosition;
    private boolean mHasNext;
    private boolean mHasPrevious;

    private SubState mIndividualStepSubState;

    private static final String VERTICAL_SAVED = "vertical";
    private static final String STEP_STRING_SAVED = "step";
    private static final String VIDEO_POSITION_SAVED = "video_position";
    private static final String HASNEXT_SAVED = "has_next";
    private static final String HASPREVIOUS_SAVED = "has_previous";

    private static final String fSubStateKey = "substate";

    public static final long fNoVideoPosition = 0;

    private static final String fBackstackTag = "INDIVIDUAL";

    public static final String TAG = "IndividualStepFragment";

    public String getBackStackTag(){
        return fBackstackTag;
    }

    @BindView(R.id.playerView) PlayerView mPlayerView;
    @BindView(R.id.recipe_step_text) TextView mRecipeTextView;
    @BindView(R.id.next_step_button) Button mNextButton;
    @BindView(R.id.previous_step_button) Button mPreviousButton;

    private SimpleExoPlayer mExoPlayer;
    private static MediaSessionCompat mMediaSession;
    private PlaybackStateCompat.Builder mStateBuilder;

    private MainActivity mParentActivity;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        if(savedInstanceState != null){
            restoreFromSavedInstance(savedInstanceState);
        }

        mParentActivity = (MainActivity) getActivity();

        View rootView;
        if(mIsVertical){
            rootView = inflater.inflate(R.layout.recipe_step_vertical, container, false);
        }
        else{
            rootView = inflater.inflate(R.layout.recipe_step_horizontal, container, false);
        }

        ButterKnife.bind(this, rootView);
        fillUI();

        updateParent();

        initialMediaSetup(rootView);

        return rootView;
    }

    private void restoreFromSavedInstance(Bundle saved){
        mIsVertical = saved.getBoolean(VERTICAL_SAVED);
        mStep = RawJsonReader.parseIndividualStepFromString(saved.getString(STEP_STRING_SAVED));
        mCurrentVideoPosition = saved.getLong(VIDEO_POSITION_SAVED);
        mHasNext = saved.getBoolean(HASNEXT_SAVED);
        mHasPrevious = saved.getBoolean(HASPREVIOUS_SAVED);

        mIndividualStepSubState = saved.getParcelable(fSubStateKey);
    }

    public void fillParameters(StateManagement state){
        mIndividualStepSubState = state.getSubState().copy();

        boolean isVertical = mIndividualStepSubState.getScreen() == StateManagement.ScreenMode.LANDSCAPE ? false : true;
        Recipe currentRecipe = state.getCurrentRecipe();
        int index = mIndividualStepSubState.getStepIndex();
        Step currentStep = currentRecipe.getSteps().get(index);
        boolean hasNext = index <= currentRecipe.getSteps().size() - 1;
        boolean hasPrevious = index > 0;

        fillParameters(isVertical, currentStep, hasNext, hasPrevious);
    }

    // pass it in directly from the initial creation
    public void fillParameters(
                               boolean isVertical,
                               Step step,
                               boolean hasNext,
                               boolean hasPrevious)
    {
        mIsVertical = isVertical;
        mStep = step;
        mHasNext = hasNext;
        mHasPrevious = hasPrevious;
        mCurrentVideoPosition = fNoVideoPosition;
    }

    private void fillUI(){
        mRecipeTextView.setText(mStep.getFullDescription());

        if(!mHasNext){
            mNextButton.setVisibility(View.GONE);
        }
        else{
            mNextButton.setOnClickListener(this);
        }

        if(!mHasPrevious){
            mPreviousButton.setVisibility(View.GONE);
        }
        else{
            mPreviousButton.setOnClickListener(this);
        }
    }

    private void updateParent(){
        mParentActivity.updateFromCurrentFragment(mIndividualStepSubState);
    }

    @Override
    public void onClick(View v) {
        Button clicked = (Button) v;

        // determine which button was clicked...
        if(clicked.getId() == R.id.previous_step_button){
            mParentActivity.processUserAction(StateManagement.UserAction.PREVIOUS_STEP, mIndividualStepSubState.getStepIndex() - 1);
        }
        else if(clicked.getId() == R.id.next_step_button){
            mParentActivity.processUserAction(StateManagement.UserAction.NEXT_STEP, mIndividualStepSubState.getStepIndex() + 1);
        }
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean(VERTICAL_SAVED, mIsVertical);
        outState.putString(STEP_STRING_SAVED, RawJsonReader.createJSONStringFromIndividualStep(mStep));
        outState.putLong(VIDEO_POSITION_SAVED, mCurrentVideoPosition);
        outState.putBoolean(HASNEXT_SAVED, mHasNext);
        outState.putBoolean(HASPREVIOUS_SAVED, mHasPrevious);

        outState.putParcelable(fSubStateKey, mIndividualStepSubState);
    }

    /////////////////////////////
    //
    //
    //
    // EXO Player interaction
    //
    //
    //
    /////////////////////////////

    private void initialMediaSetup(View rootView){

        // Initialize the player view.
        mPlayerView = rootView.findViewById(R.id.playerView);

        mPlayerView.setDefaultArtwork(BitmapFactory.decodeResource
                (getResources(), R.drawable.question_mark));

        // Initialize the Media Session.
        initializeMediaSession();

        // Initialize the player.
        initializePlayer(Uri.parse(mStep.getVideoURL()));
    }

    /**
     * Initializes the Media Session to be enabled with media buttons, transport controls, callbacks
     * and media controller.
     */
    private void initializeMediaSession() {

        // Create a MediaSessionCompat.
        mMediaSession = new MediaSessionCompat(mParentActivity, TAG);

        // Enable callbacks from MediaButtons and TransportControls.
        mMediaSession.setFlags(
                MediaSessionCompat.FLAG_HANDLES_MEDIA_BUTTONS |
                        MediaSessionCompat.FLAG_HANDLES_TRANSPORT_CONTROLS);

        // Do not let MediaButtons restart the player when the app is not visible.
        mMediaSession.setMediaButtonReceiver(null);

        // Set an initial PlaybackState with ACTION_PLAY, so media buttons can start the player.
        mStateBuilder = new PlaybackStateCompat.Builder()
                .setActions(
                        PlaybackStateCompat.ACTION_PLAY |
                                PlaybackStateCompat.ACTION_PAUSE |
                                PlaybackStateCompat.ACTION_SKIP_TO_PREVIOUS |
                                PlaybackStateCompat.ACTION_PLAY_PAUSE);

        mMediaSession.setPlaybackState(mStateBuilder.build());


        // MySessionCallback has methods that handle callbacks from a media controller.
        mMediaSession.setCallback(new MySessionCallback());

        // Start the Media Session since the activity is active.
        mMediaSession.setActive(true);

    }

    /**
     * Initialize ExoPlayer.
     * @param mediaUri The URI of the sample to play.
     */
    private void initializePlayer(Uri mediaUri) {
        if (mExoPlayer == null) {
            // Create an instance of the ExoPlayer.
            TrackSelector trackSelector = new DefaultTrackSelector();
            LoadControl loadControl = new DefaultLoadControl();
            DefaultRenderersFactory renderersFactory = new DefaultRenderersFactory(mParentActivity);

            mExoPlayer = ExoPlayerFactory.newSimpleInstance(renderersFactory, trackSelector, loadControl);
            mPlayerView.setPlayer(mExoPlayer);

            // Set the ExoPlayer.EventListener to this activity.
            mExoPlayer.addListener(this);

            /*
            // Prepare the MediaSource.
            String userAgent = Util.getUserAgent(mParentActivity, "BakingTime");

            MediaSource mediaSource =
            new ExtractorMediaSource(
                    mediaUri,
                    new DefaultDataSourceFactory(
                            mParentActivity,
                            userAgent),
                    new DefaultExtractorsFactory(),
                    null,
                    null);
             */

            DefaultDataSourceFactory dataSourceFactory = new DefaultDataSourceFactory(mParentActivity, "BakingTime");
            DefaultExtractorsFactory extractorsFactory = new DefaultExtractorsFactory();

            MediaSource mediaSource = new ExtractorMediaSource.Factory(dataSourceFactory).setExtractorsFactory(extractorsFactory).createMediaSource(mediaUri);

            mExoPlayer.prepare(mediaSource);
            mExoPlayer.setPlayWhenReady(true);
        }
    }

    //
    //
    // winding the whole thing down
    //
    //

    /**
     * Release ExoPlayer.
     */
    private void releasePlayer() {
        mExoPlayer.stop();
        mExoPlayer.release();
        mExoPlayer = null;
    }


    @Override
    public void onPause() {
        super.onPause();
        releasePlayer();
        mMediaSession.setActive(false);
    }

    //
    //
    //
    //
    // ExoPlayer Event Listeners
    //
    //
    //
    //

    @Override
    public void onTracksChanged(TrackGroupArray trackGroups, TrackSelectionArray trackSelections) {
    }

    @Override
    public void onLoadingChanged(boolean isLoading) {    }

    @Override
    public void onShuffleModeEnabledChanged(boolean shuffleModeEnabled) {    }

    @Override
    public void onRepeatModeChanged(int repeatMode) {    }

    @Override
    public void onPositionDiscontinuity(int reason) {    }

    @Override
    public void onTimelineChanged(Timeline timeline, Object manifest, int reason) {    }

    @Override
    public void onPlaybackParametersChanged(PlaybackParameters playbackParameters) {    }

    /**
     * Method that is called when the ExoPlayer state changes. Used to update the MediaSession
     * PlayBackState to keep in sync, and post the media notification.
     * @param playWhenReady true if ExoPlayer is playing, false if it's paused.
     * @param playbackState int describing the state of ExoPlayer. Can be STATE_READY, STATE_IDLE,
     *                      STATE_BUFFERING, or STATE_ENDED.
     */
    @Override
    public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {
        if((playbackState == ExoPlayer.STATE_READY) && playWhenReady){
            mStateBuilder.setState(PlaybackStateCompat.STATE_PLAYING,
                    mExoPlayer.getCurrentPosition(), 1f);
        } else if((playbackState == ExoPlayer.STATE_READY)){
            mStateBuilder.setState(PlaybackStateCompat.STATE_PAUSED,
                    mExoPlayer.getCurrentPosition(), 1f);
        }
        mMediaSession.setPlaybackState(mStateBuilder.build());
    }

    @Override
    public void onPlayerError(ExoPlaybackException error) {
    }

    @Override
    public void onSeekProcessed() {

    }

    /**
     * Media Session Callbacks, where all external clients control the player.
     */
    private class MySessionCallback extends MediaSessionCompat.Callback {
        @Override
        public void onPlay() {
            mExoPlayer.setPlayWhenReady(true);
        }

        @Override
        public void onPause() {
            mExoPlayer.setPlayWhenReady(false);
        }

        @Override
        public void onSkipToPrevious() {
            mExoPlayer.seekTo(0);
        }
    }

    /**
     * Broadcast Receiver registered to receive the MEDIA_BUTTON intent coming from clients.
     */
    public static class MediaReceiver extends BroadcastReceiver {

        public MediaReceiver() {
        }

        @Override
        public void onReceive(Context context, Intent intent) {
            MediaButtonReceiver.handleIntent(mMediaSession, intent);
        }
    }
}
