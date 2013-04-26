package id.ac.amikom.amikomsocial.libs;

import java.util.HashMap;
import java.util.List;

import android.content.Context;
import android.text.Html;
import android.text.Spanned;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SimpleAdapter;
import android.widget.TextView;

public class CustomAdapter extends SimpleAdapter {
	private int[] colors = new int[] { 0x30E7E7E7, 0x30FFFFFF };
	private Context c;
	
	public CustomAdapter(Context context, List<HashMap<String, Object>> items,
			int resource, String[] from, int[] to) {
		super(context, items, resource, from, to);
		c = context;
	}

	@Override
	public void setViewText(TextView view, String text) {
		URLImageParser p = new URLImageParser(view, c);
		Spanned htmlSpan = Html.fromHtml(text, p, null);
		view.setText(htmlSpan);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View view = super.getView(position, convertView, parent);
		int colorPos = position % colors.length;
		view.setBackgroundColor(colors[colorPos]);

		return view;
	}

}