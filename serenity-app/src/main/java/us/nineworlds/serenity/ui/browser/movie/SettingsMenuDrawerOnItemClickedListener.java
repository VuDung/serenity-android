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

package us.nineworlds.serenity.ui.browser.movie;

import us.nineworlds.serenity.ui.activity.SerenityMultiViewVideoActivity;
import us.nineworlds.serenity.ui.preferences.SerenityPreferenceActivity;
import us.nineworlds.serenity.widgets.DrawerLayout;
import android.content.Intent;
import android.view.View;
import android.view.View.OnClickListener;

public class SettingsMenuDrawerOnItemClickedListener implements OnClickListener {
	private final DrawerLayout menuDrawer;

	public SettingsMenuDrawerOnItemClickedListener(DrawerLayout drawer) {
		menuDrawer = drawer;
	}

	@Override
	public void onClick(View view) {
		SerenityMultiViewVideoActivity activity = (SerenityMultiViewVideoActivity) view
				.getContext();

		Intent i = new Intent(activity, SerenityPreferenceActivity.class);
		activity.startActivity(i);
		menuDrawer.closeDrawers();
	}
}
