package id.ac.amikom.amikomsocial;

import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import com.markupartist.android.widget.ActionBar;
import com.markupartist.android.widget.ActionBar.AbstractAction;
import com.markupartist.android.widget.ActionBar.IntentAction;

import android.app.TabActivity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.InflateException;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.widget.PopupMenu;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.TabHost.TabSpec;

@SuppressWarnings("deprecation")
public class MainActivity extends TabActivity {

	private TabHost mTabHost;

	private void setupTabHost() {
		mTabHost = (TabHost) findViewById(android.R.id.tabhost);
		mTabHost.setup();
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_tabs);

		final ActionBar actionBar = (ActionBar) findViewById(R.id.actionbar);
		actionBar.setTitle(R.string.app_title);

		actionBar.setHomeAction(new IntentAction(this, MainActivity
				.createIntent(this), R.drawable.logo_actionbar));
		actionBar.addAction(new IntentAction(this, MainActivity
				.createIntent(this), R.drawable.ic_action_edit));

		if (Build.VERSION.SDK_INT >= 11)
			actionBar.addAction(new MenuAction());

		setupTabHost();
		mTabHost.getTabWidget().setDividerDrawable(R.drawable.tab_divider);

		setupTab(new TextView(this), "SHOUT", "SampleActivity.class");
		setupTab(new TextView(this), "@YOU", "SampleActivity.class");
		setupTab(new TextView(this), "MESSAGE", "SampleActivity.class");

	}

	private void setupTab(final View view, final String tag,
			final String className) {
		View tabview = createTabView(mTabHost.getContext(), tag);

		Intent intent;
		intent = new Intent().setClass(this, SampleActivity.class);
		if (className.equals("SampleActivity.class")) {
			intent = new Intent().setClass(this, SampleActivity.class);
		}
		if (className.equals("SampleActivity.class")) {
			intent = new Intent().setClass(this, SampleActivity.class);
		}
		if (className.equals("SampleActivity.class")) {
			intent = new Intent().setClass(this, SampleActivity.class);
		}

		TabSpec setContent = mTabHost.newTabSpec(tag).setIndicator(tabview)
				.setContent(intent);{ };

		mTabHost.addTab(setContent);
	}

	private static View createTabView(final Context context, final String text) {
		View view = LayoutInflater.from(context)
				.inflate(R.layout.tabs_bg, null);
		TextView tv = (TextView) view.findViewById(R.id.tabsText);
		tv.setText(text);
		return view;
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.menu_main, menu);
		//setMenuBackground();
		return true;
	}

	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		if (Build.VERSION.SDK_INT >= 11) {
			menu.setGroupVisible(0, false);
		}

		return true;
	}

	public static Intent createIntent(Context context) {
		Intent i = new Intent(context, MainActivity.class);
		i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		return i;
	}

	private class MenuAction extends AbstractAction {

		public MenuAction() {
			super(R.drawable.ic_action_overflow);
		}

		public void performAction(View view) {

			PopupMenu popup = new PopupMenu(getApplicationContext(), view);
			MenuInflater inflater = popup.getMenuInflater();
			inflater.inflate(R.menu.menu_main, popup.getMenu());
			popup.show();
		}

	}

	private void setMenuBackground() {

		getLayoutInflater().setFactory(new LayoutInflater.Factory() {

			public View onCreateView(final String name, final Context context,
					final AttributeSet attributeSet) {

				if (name.equalsIgnoreCase("com.android.internal.view.menu.IconMenuItemView")) {
					try {

						final LayoutInflater f = getLayoutInflater();
						final View[] view = new View[1];
						try {
							view[0] = f.createView(name, null, attributeSet);
						} catch (InflateException e) {
							//hackAndroid23(name, attributeSet, f, view);
						}

						new Handler().post(new Runnable() {
							public void run() {
								view[0].setBackgroundResource(R.drawable.menu_selector);
								((TextView) view[0]).setTextColor(Color.WHITE);
							}
						});
						return view[0];

					} catch (final Exception e) {
					}
				}
				return null;
			}

		});
	}

	static void hackAndroid23(final String name,
			final android.util.AttributeSet attrs, final LayoutInflater f,
			final View[] view) {
		try {
			f.inflate(new XmlPullParser() {
				public int next() throws XmlPullParserException, IOException {
					try {
						view[0] = (TextView) f.createView(name, null, attrs);
					} catch (InflateException e) {
					} catch (ClassNotFoundException e) {
					}
					throw new XmlPullParserException("exit");
				}

				public void defineEntityReplacementText(String entityName,
						String replacementText) throws XmlPullParserException {
					// TODO Auto-generated method stub

				}

				public int getAttributeCount() {
					// TODO Auto-generated method stub
					return 0;
				}

				public String getAttributeName(int arg0) {
					// TODO Auto-generated method stub
					return null;
				}

				public String getAttributeNamespace(int arg0) {
					// TODO Auto-generated method stub
					return null;
				}

				public String getAttributePrefix(int arg0) {
					// TODO Auto-generated method stub
					return null;
				}

				public String getAttributeType(int arg0) {
					// TODO Auto-generated method stub
					return null;
				}

				public String getAttributeValue(int arg0) {
					// TODO Auto-generated method stub
					return null;
				}

				public String getAttributeValue(String arg0, String arg1) {
					// TODO Auto-generated method stub
					return null;
				}

				public int getColumnNumber() {
					// TODO Auto-generated method stub
					return 0;
				}

				public int getDepth() {
					// TODO Auto-generated method stub
					return 0;
				}

				public int getEventType() throws XmlPullParserException {
					// TODO Auto-generated method stub
					return 0;
				}

				public boolean getFeature(String arg0) {
					// TODO Auto-generated method stub
					return false;
				}

				public String getInputEncoding() {
					// TODO Auto-generated method stub
					return null;
				}

				public int getLineNumber() {
					// TODO Auto-generated method stub
					return 0;
				}

				public String getName() {
					// TODO Auto-generated method stub
					return null;
				}

				public String getNamespace() {
					// TODO Auto-generated method stub
					return null;
				}

				public String getNamespace(String arg0) {
					// TODO Auto-generated method stub
					return null;
				}

				public int getNamespaceCount(int arg0)
						throws XmlPullParserException {
					// TODO Auto-generated method stub
					return 0;
				}

				public String getNamespacePrefix(int arg0)
						throws XmlPullParserException {
					// TODO Auto-generated method stub
					return null;
				}

				public String getNamespaceUri(int arg0)
						throws XmlPullParserException {
					// TODO Auto-generated method stub
					return null;
				}

				public String getPositionDescription() {
					// TODO Auto-generated method stub
					return null;
				}

				public String getPrefix() {
					// TODO Auto-generated method stub
					return null;
				}

				public Object getProperty(String arg0) {
					// TODO Auto-generated method stub
					return null;
				}

				public String getText() {
					// TODO Auto-generated method stub
					return null;
				}

				public char[] getTextCharacters(int[] arg0) {
					// TODO Auto-generated method stub
					return null;
				}

				public boolean isAttributeDefault(int arg0) {
					// TODO Auto-generated method stub
					return false;
				}

				public boolean isEmptyElementTag()
						throws XmlPullParserException {
					// TODO Auto-generated method stub
					return false;
				}

				public boolean isWhitespace() throws XmlPullParserException {
					// TODO Auto-generated method stub
					return false;
				}

				public int nextTag() throws XmlPullParserException, IOException {
					// TODO Auto-generated method stub
					return 0;
				}

				public String nextText() throws XmlPullParserException,
						IOException {
					// TODO Auto-generated method stub
					return null;
				}

				public int nextToken() throws XmlPullParserException,
						IOException {
					// TODO Auto-generated method stub
					return 0;
				}

				public void require(int arg0, String arg1, String arg2)
						throws XmlPullParserException, IOException {
					// TODO Auto-generated method stub

				}

				public void setFeature(String arg0, boolean arg1)
						throws XmlPullParserException {
					// TODO Auto-generated method stub

				}

				public void setInput(Reader arg0) throws XmlPullParserException {
					// TODO Auto-generated method stub

				}

				public void setInput(InputStream arg0, String arg1)
						throws XmlPullParserException {
					// TODO Auto-generated method stub

				}

				public void setProperty(String arg0, Object arg1)
						throws XmlPullParserException {
					// TODO Auto-generated method stub

				}

			}, null, false);
		} catch (InflateException e1) {

		}
	}

}
