﻿using Android.App;
using Android.Content.PM;
using Microsoft.Maui.Controls.PlatformConfiguration;

namespace MOIT151.Mobile;

[Activity(NoHistory = true, LaunchMode = LaunchMode.SingleTop, Exported = true)]
[IntentFilter(new[] { Android.Content.Intent.ActionView },
    Categories = new[] { 
        Android.Content.Intent.CategoryDefault,
        Android.Content.Intent.CategoryBrowsable 
    },
    DataScheme = CALLBACK_SCHEME)]
public class WebAuthnCallbackActivity : WebAuthenticatorCallbackActivity
{
    const string CALLBACK_SCHEME = "moit151";
}