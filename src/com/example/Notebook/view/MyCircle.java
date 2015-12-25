package com.example.Notebook.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;


public class MyCircle extends View {

	private int radius = 5;
	private	int count=0;
	
	private int choosePosition = 0;

	private int gap = 20;
	
	public  void setCount(int cnt){
		count=cnt;
	}
	
	public	void	choose(int pos){
		choosePosition=pos;
		
		this.invalidate();
	}

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);

		Paint p = new Paint();
		p.setAntiAlias(true);

		int w = this.getWidth();
		int h = this.getHeight();
		
		int start_x = (w-(count-1)*gap )/2;

		for (int i = 0; i < count; i++) {
			if (choosePosition == i) {
				p.setColor(Color.RED);
				canvas.drawCircle(start_x + i * gap, h-50, radius+3, p);
			} else {
				p.setColor(Color.DKGRAY);
				canvas.drawCircle(start_x + i * gap, h-50, radius, p);
			}
		}
	}

	public MyCircle(Context context, AttributeSet attrs) {
		super(context, attrs);
	}
}
