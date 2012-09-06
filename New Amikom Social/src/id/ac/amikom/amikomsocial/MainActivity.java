package id.ac.amikom.amikomsocial;

import com.markupartist.android.widget.ActionBar;
import com.markupartist.android.widget.ActionBar.AbstractAction;
import com.markupartist.android.widget.ActionBar.IntentAction;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.widget.PopupMenu;
import android.widget.TextView;

public class MainActivity extends Activity {

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		final ActionBar actionBar = (ActionBar) findViewById(R.id.actionbar);
		actionBar.setTitle(R.string.app_title);

		actionBar.setHomeAction(new IntentAction(this, MainActivity
				.createIntent(this), R.drawable.logo_actionbar));
		actionBar.addAction(new IntentAction(this, MainActivity
				.createIntent(this), R.drawable.ic_action_edit));

		if (Build.VERSION.SDK_INT >= 11) {
			actionBar.addAction(new MenuAction());

		}

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.menu_main, menu);
		setMenuBackground();
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
						final View view = f
								.createView(name, null, attributeSet);

						new Handler().post(new Runnable() {
							public void run() {								
								view.setBackgroundResource(R.drawable.menu_selector);  
                                ((TextView) view).setTextColor(Color.WHITE);
							}
						});
						return view;
					} catch (final Exception e) {

					}
				}
				return null;
			}

		});
	}

}
