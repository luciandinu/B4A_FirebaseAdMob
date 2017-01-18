package anywheresoftware.b4a.admobwrapper;

import anywheresoftware.b4a.AbsObjectWrapper;
import anywheresoftware.b4a.BA;
import anywheresoftware.b4a.BA.ActivityObject;
import anywheresoftware.b4a.BA.DependsOn;
import anywheresoftware.b4a.BA.DontInheritEvents;
import anywheresoftware.b4a.BA.Events;
import anywheresoftware.b4a.BA.Permissions;
import anywheresoftware.b4a.BA.ShortName;
import anywheresoftware.b4a.BA.Version;
import anywheresoftware.b4a.keywords.Common;
import anywheresoftware.b4a.objects.ViewWrapper;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.NativeExpressAdView;
import com.google.android.gms.ads.reward.RewardItem;
import com.google.android.gms.ads.reward.RewardedVideoAd;
import com.google.android.gms.ads.reward.RewardedVideoAdListener;

@Version(1.31f)
@ShortName("AdView")
@Events(values={"ReceiveAd", "FailedToReceiveAd (ErrorCode As String)",
		"AdScreenDismissed", "PresentScreen"})
	@ActivityObject
	@DontInheritEvents
	@Permissions(values={"android.permission.INTERNET", "android.permission.ACCESS_NETWORK_STATE"})
	@DependsOn(values={"com.google.firebase:firebase-ads"})
	public class AdViewWrapper extends ViewWrapper<AdView> {
	/**
	 * 320dip x 50dip (default size)
	 */
	public static Object SIZE_BANNER = AdSize.BANNER;
	/**
	 * 468dip x 60dip - tablet only
	 */
	public static Object SIZE_IAB_BANNER = AdSize.FULL_BANNER;
	/**
	 * 728dip x 90dip - tablet only
	 */
	public static Object SIZE_IAB_LEADERBOARD = AdSize.LEADERBOARD;
	/**
	 * 300dip x 250dip - tablet only
	 */
	public static Object SIZE_IAB_MRECT = AdSize.MEDIUM_RECTANGLE;
	/**
	 * Ad will use the full available width automatically.
	 *You can use this code to add such an ad to the bottom of the screen:
	 *<code>
	 *Adview1.Initialize2("Ad", "xxxxxxxx", AdView1.SIZE_SMART_BANNER)
	 *Dim height As Int
	 *If GetDeviceLayoutValues.ApproximateScreenSize < 6 Then
	 *    'phones
	 *    If 100%x > 100%y Then height = 32dip Else height = 50dip
	 *Else
	 *    'tablets
	 *    height = 90dip
	 *End If
	 *Activity.AddView(AdView1, 0dip, 100%y - height, 100%x, height)</code>
	 */
	public static Object SIZE_SMART_BANNER = AdSize.SMART_BANNER;
	/**
	 * Initializes the AdView using the default 320dip x 50dip size.
	 *EventName - Name of Subs that will handle the events.
	 *PublisherId - The publisher id you received from AdMob.
	 */
	public void Initialize(final BA ba, String EventName, String PublisherId) {
		Initialize2(ba, EventName, PublisherId, AdSize.BANNER);
	}
	/**
	 * Initializes the AdView.
	 *EventName - Name of Subs that will handle the events.
	 *AdUnitId - The Ad unit id received from AdMob.
	 *Size - One of the SIZE constants.
	 */
	public void Initialize2(final BA ba, String EventName, String AdUnitId, Object Size) {
		AdView ad = new AdView(ba.activity);
		ad.setAdSize((com.google.android.gms.ads.AdSize)Size);
		ad.setAdUnitId(AdUnitId);
		setObject(ad);
		super.Initialize(ba, EventName);
		final String eventName = EventName.toLowerCase(BA.cul);
		getObject().setAdListener(new AdListener() {

			@Override
			public void onAdFailedToLoad(int e){
				ba.raiseEvent(getObject(), eventName + "_failedtoreceivead", String.valueOf(e));
			}
			@Override
			public void onAdLoaded() {
				ba.raiseEvent(getObject(), eventName + "_receivead");
			}
			@Override
			public void onAdClosed() {
				ba.raiseEventFromDifferentThread(getObject(), null, 0, eventName + "_adscreendismissed", false, null);
			}
			@Override
			public void onAdLeftApplication() {
				//
			}
			@Override
			public void onAdOpened() {
				ba.raiseEventFromDifferentThread(getObject(), null, 0, eventName + "_presentscreen", false, null);

			}
		});
	}

	/**
	 * Sends a request to AdMob, requesting an ad.
	 */
	public void LoadAd() {
		com.google.android.gms.ads.AdRequest req = new com.google.android.gms.ads.AdRequest.Builder().build();
		getObject().loadAd(req);
	}
	/**
	 * Requests an ad.
	 *TestDevice - The test device id. You can see the id in the unfiltered logs.
	 */
	public void LoadAdWithTestDevice(String TestDevice) {
		com.google.android.gms.ads.AdRequest req = new com.google.android.gms.ads.AdRequest.Builder().addTestDevice(TestDevice).build();
		getObject().loadAd(req);
	}

	/**
	 *Should be called from Activity_Pause. 
	 */
	public void Pause() {
		getObject().pause();
	}
	/**
	 *Should be called from Activity_Resume.
	 */
	public void Resume() {
		getObject().resume();
	}
	
	@ShortName("NativeExpressAd")
	@ActivityObject
	@DontInheritEvents
	@Permissions(values={"android.permission.INTERNET", "android.permission.ACCESS_NETWORK_STATE"})
	public static class NativeExpressAdWrapper extends ViewWrapper<NativeExpressAdView> {
		/**
		 * Initializes the ad view.
		 *EventName - Sets the subs that will handle the events.
		 *AdUnitId - Ad unit ID of a native ad.
		 *Width - Requested ad width.
		 *Height - Requested ad height.
		 */
		public void Initialize(final BA ba, String EventName, String AdUnitId, float Width, float Height) {
			NativeExpressAdView ad = new NativeExpressAdView(ba.activity);
			ad.setAdSize(new AdSize((int)Math.round(Width / Common.Density), (int)Math.round(Height / Common.Density)));
			ad.setAdUnitId(AdUnitId);
			setObject(ad);
			super.Initialize(ba, EventName);
			final String eventName = EventName.toLowerCase(BA.cul);
			getObject().setAdListener(new AdListener() {

				@Override
				public void onAdFailedToLoad(int e){
					ba.raiseEvent(getObject(), eventName + "_failedtoreceivead", String.valueOf(e));
				}
				@Override
				public void onAdLoaded() {
					ba.raiseEvent(getObject(), eventName + "_receivead");
				}
				@Override
				public void onAdClosed() {
					ba.raiseEventFromDifferentThread(getObject(), null, 0, eventName + "_adscreendismissed", false, null);
				}
				@Override
				public void onAdLeftApplication() {
					//
				}
				@Override
				public void onAdOpened() {
					ba.raiseEventFromDifferentThread(getObject(), null, 0, eventName + "_presentscreen", false, null);

				}
			});
		}
		/**
		 * Sends a request to AdMob, requesting an ad.
		 */
		public void LoadAd() {
			com.google.android.gms.ads.AdRequest req = new com.google.android.gms.ads.AdRequest.Builder().build();
			getObject().loadAd(req);
		}
		/**
		 * Requests an ad.
		 *TestDevice - The test device id. You can see the id in the unfiltered logs.
		 */
		public void LoadAdWithTestDevice(String TestDevice) {
			com.google.android.gms.ads.AdRequest req = new com.google.android.gms.ads.AdRequest.Builder().addTestDevice(TestDevice).build();
			getObject().loadAd(req);
		}

		/**
		 *Should be called from Activity_Pause. 
		 */
		public void Pause() {
			getObject().pause();
		}
		/**
		 *Should be called from Activity_Resume.
		 */
		public void Resume() {
			getObject().resume();
		}
	}
	/**
	 *Interstitial ads are full screen ads.
	 *The following events are raised:
	 *ReceiveAd - An ad is ready to be shown.
	 *FailedToReceiveAd
	 *AdOpened - The ad has become visible.
	 *AdClosed - The user has closed the ad.
	 *AdLeftApplication - The user has clicked on the ad.
	 *Example of both types of ads using test ids:
	 *<code>
	 *Sub Process_Globals
	 *
	 *End Sub
	 *
	 *Sub Globals
	 *	Private BannerAd As AdView
	 *	Private IAd As InterstitialAd
	 *End Sub
	 *
	 *Sub Activity_Create(FirstTime As Boolean)
	 *	Activity.LoadLayout("1")
	 *	BannerAd.Initialize2("BannerAd", "ca-app-pub-3940256099942544/6300978111", BannerAd.SIZE_SMART_BANNER)
	 *	Dim height As Int
	 *	If GetDeviceLayoutValues.ApproximateScreenSize < 6 Then
	 *	    'phones
	 *	    If 100%x > 100%y Then height = 32dip Else height = 50dip
	 *	Else
	 *	    'tablets
	 *	    height = 90dip
	 *	End If
	 *	Activity.AddView(BannerAd, 0dip, 100%y - height, 100%x, height)
	 *	BannerAd.LoadAd
	 *	IAd.Initialize("iad", "ca-app-pub-3940256099942544/1033173712")
	 *End Sub
	 *
	 *Sub Activity_Resume
	 *	IAd.LoadAd
	 *End Sub
	 *
	 *Sub Activity_Pause (UserClosed As Boolean)
	 *
	 *End Sub
	 *
	 *Sub Activity_Click
	 *	If IAd.Ready Then IAd.Show Else IAd.LoadAd
	 *End Sub</code>
	 */
	@ShortName("InterstitialAd")
	@Events(values={"ReceiveAd", "FailedToReceiveAd (ErrorCode As String)",
			"AdClosed", "AdOpened", "AdLeftApplication"})
	@ActivityObject
	public static class InterstitialAdWrapper extends AbsObjectWrapper<InterstitialAd> {
		/**
		 * Initializes the object.
		 *EventName - Set the subs that will handle the events.
		 *AdUnitId - The ad unit id. Test id: ca-app-pub-3940256099942544/1033173712
		 */
		public void Initialize(final BA ba, String EventName, String AdUnitId) {
			final String eventName = EventName.toLowerCase(BA.cul);
			InterstitialAd ad = new InterstitialAd(ba.context);
			ad.setAdUnitId(AdUnitId);
			ad.setAdListener(new AdListener() {

				@Override
				public void onAdClosed() {
					ba.raiseEventFromDifferentThread(getObject(), null, 0, eventName + "_adclosed", false, null);
				}
				@Override
				public void onAdFailedToLoad(int arg0) {
					ba.raiseEventFromDifferentThread(getObject(), null, 0, eventName + "_failedtoreceivead", false, new Object[] {String.valueOf(arg0)});
				}
				@Override
				public void onAdLeftApplication() {
					ba.raiseEventFromDifferentThread(getObject(), null, 0, eventName + "_adleftapplication", false, null);
				}
				@Override
				public void onAdOpened() {
					ba.raiseEventFromDifferentThread(getObject(), null, 0, eventName + "_adopened", false, null);
				}
				@Override
				public void onAdLoaded() {
					ba.raiseEventFromDifferentThread(getObject(), null, 0, eventName + "_receivead", false, null);
				}
			});
			setObject(ad);
			
		}
		/**
		 * Requests an ad. The ReceiveAd or FailedToReceiveAd events will be raised.
		 */
		public void LoadAd() {
			getObject().loadAd(new AdRequest.Builder().build());
		}
		/**
		 * Tests whether there is an ad ready to be shown.
		 */
		public boolean getReady() {
			return getObject().isLoaded();
		}
		/**
		 * Shows the loaded ad.
		 */
		public void Show() {
			getObject().show();
		}
	}
	/**
	 * A video ad where the user is rewarded if it is watched fully. The Rewarded event will be raised in that case.
	 *Note that AdMob does not serve these ads directly. You need to use the mediation feature to add an ad network that supports this type of ads.
	 */
	@ShortName("RewardedVideoAd")
	@Events(values={"ReceiveAd", "FailedToReceiveAd (ErrorCode As String)",
			"AdClosed", "AdOpened", "AdLeftApplication", "Rewarded (Item As Object)"})
	@ActivityObject
	public static class RewardedVideoAdWrapper extends AbsObjectWrapper<RewardedVideoAd> {
		public void Initialize(final BA ba, String EventName) { 
			final String eventName = EventName.toLowerCase(BA.cul);
			RewardedVideoAd ad = MobileAds.getRewardedVideoAdInstance(ba.context);
			ad.setRewardedVideoAdListener(new RewardedVideoAdListener() {

				@Override
				public void onRewarded(RewardItem arg0) {
					ba.raiseEventFromDifferentThread(getObject(), null, 0, eventName + "_rewarded", false, new Object[] {arg0});
					
				}

				@Override
				public void onRewardedVideoAdClosed() {
					ba.raiseEventFromDifferentThread(getObject(), null, 0, eventName + "_adclosed", false, null);
					
				}

				@Override
				public void onRewardedVideoAdFailedToLoad(int arg0) {
					ba.raiseEventFromDifferentThread(getObject(), null, 0, eventName + "_failedtoreceivead", false, new Object[] {String.valueOf(arg0)});
					
				}

				@Override
				public void onRewardedVideoAdLeftApplication() {
					ba.raiseEventFromDifferentThread(getObject(), null, 0, eventName + "_adleftapplication", false, null);
					
				}

				@Override
				public void onRewardedVideoAdLoaded() {
					ba.raiseEventFromDifferentThread(getObject(), null, 0, eventName + "_receivead", false, null);
					
				}

				@Override
				public void onRewardedVideoAdOpened() {
					ba.raiseEventFromDifferentThread(getObject(), null, 0, eventName + "_adopened", false, null);
					
				}

				@Override
				public void onRewardedVideoStarted() {
					
					
				}
				
			});
			setObject(ad);
		}
		/**
		 * Requests an ad. The ReceiveAd or FailedToReceiveAd events will be raised.
		 */
		public void LoadAd(String AdUnitId) {
			getObject().loadAd(AdUnitId, new AdRequest.Builder().build());
		}
		/**
		 * Tests whether there is an ad ready to be shown.
		 */
		public boolean getReady() {
			return getObject().isLoaded();
		}
		/**
		 * Shows the loaded ad.
		 */
		public void Show() {
			getObject().show();
		}
		
		
	}
}

