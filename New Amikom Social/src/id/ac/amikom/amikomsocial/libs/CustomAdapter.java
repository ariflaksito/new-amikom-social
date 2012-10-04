package id.ac.amikom.amikomsocial.libs;

import java.util.HashMap;
import java.util.List;

import android.content.Context;
import android.text.Html;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.TextView.BufferType;

public class CustomAdapter extends SimpleAdapter {
	private int[] colors = new int[] {  0x30E7E7E7, 0x30FFFFFF };

	public CustomAdapter(Context context, List<HashMap<String, Object>> items,
			int resource, String[] from, int[] to) {
		super(context, items, resource, from, to);
	}

	@Override
    public void setViewText (TextView view, String text) {
        view.setText(Html.fromHtml(text),BufferType.SPANNABLE);
    }
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View view = super.getView(position, convertView, parent);
		int colorPos = position % colors.length;
		view.setBackgroundColor(colors[colorPos]);							
		
		return view;
	}
	
} 