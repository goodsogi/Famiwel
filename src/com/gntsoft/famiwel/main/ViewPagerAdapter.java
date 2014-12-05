package com.gntsoft.famiwel.main;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.view.ViewGroup;

import com.gntsoft.famiwel.best.NewBestFragment;
import com.gntsoft.famiwel.check.CheckFragment;
import com.gntsoft.famiwel.welfare.WelfareFragment;
import com.gntsoft.famiwel.welpang.NewWelpangFragment;

//FragmentPagerAdapter는 이전 fragment를 메모리에 보관, FragmentStatePagerAdapter는 이전 fragment를 삭제(fragment 수가 많을 때 적합)
public class ViewPagerAdapter extends FragmentPagerAdapter {
	private static final int COUNT = 5;
	
	
    

	public ViewPagerAdapter(FragmentManager fm) {
		super(fm);

	}

	@Override
	public Fragment getItem(int position) {

		switch (position) {
		case 0:
			return new NewMainFragment();
		case 1:
			return new NewWelpangFragment();
		case 2:

			return new NewBestFragment();
		case 3:

			return new CheckFragment();
		case 4:

			return new WelfareFragment();

		}

		return null;

	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return COUNT;
	}

	@Override
	public int getItemPosition(Object object) {

		return POSITION_NONE;

	}
}
