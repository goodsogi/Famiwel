package com.gntsoft.famiwel.welfare;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.gntsoft.famiwel.FWCommonFragment;
import com.gntsoft.famiwel.FWConstants;
import com.gntsoft.famiwel.FWWebViewActivity;
import com.gntsoft.famiwel.FWWebpageUrls;
import com.gntsoft.famiwel.R;
import com.gntsoft.famiwel.main.MainActivity;
import com.pluslibrary.PlusConstants;
import com.pluslibrary.utils.PlusLogger;
import com.pluslibrary.utils.PlusOnClickListener;

public class WelfareFragment extends FWCommonFragment {

	public WelfareFragment() {
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		
		//((MainActivity)mActivity).setContainerViewVisibility(true);
		View rootView = inflater.inflate(R.layout.fragment_welfare, container,
				false);
		return rootView;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
	}

	@Override
	protected void addListenerButton() {
		Button welfare1 = (Button) mActivity.findViewById(R.id.welfare1);
		welfare1.setOnClickListener(new PlusOnClickListener() {

			@Override
			protected void doIt() {

				goToWebpage("01");

			}
		});
		Button welfare2 = (Button) mActivity.findViewById(R.id.welfare2);
		welfare2.setOnClickListener(new PlusOnClickListener() {

			@Override
			protected void doIt() {

				goToWebpage("02");

			}
		});
		Button welfare3 = (Button) mActivity.findViewById(R.id.welfare3);
		welfare3.setOnClickListener(new PlusOnClickListener() {

			@Override
			protected void doIt() {

				goToWebpage("03");

			}
		});
		Button welfare4 = (Button) mActivity.findViewById(R.id.welfare4);
		welfare4.setOnClickListener(new PlusOnClickListener() {

			@Override
			protected void doIt() {

				goToWebpage("04");

			}
		});
		Button welfare5 = (Button) mActivity.findViewById(R.id.welfare5);
		welfare5.setOnClickListener(new PlusOnClickListener() {

			@Override
			protected void doIt() {

				goToWebpage("05");

			}
		});
		Button welfare6 = (Button) mActivity.findViewById(R.id.welfare6);
		welfare6.setOnClickListener(new PlusOnClickListener() {

			@Override
			protected void doIt() {

				goToWebpage("06");

			}
		});
		Button welfare7 = (Button) mActivity.findViewById(R.id.welfare7);
		welfare7.setOnClickListener(new PlusOnClickListener() {

			@Override
			protected void doIt() {

				goToWebpage("07");

			}
		});
		Button welfare8 = (Button) mActivity.findViewById(R.id.welfare8);
		welfare8.setOnClickListener(new PlusOnClickListener() {

			@Override
			protected void doIt() {

				goToWebpage("08");

			}
		});
		Button welfare9 = (Button) mActivity.findViewById(R.id.welfare9);
		welfare9.setOnClickListener(new PlusOnClickListener() {

			@Override
			protected void doIt() {

				goToWebpage("09");

			}
		});
		Button welfare10 = (Button) mActivity.findViewById(R.id.welfare10);
		welfare10.setOnClickListener(new PlusOnClickListener() {

			@Override
			protected void doIt() {

				goToWebpage("10");

			}
		});
		Button welfare11 = (Button) mActivity.findViewById(R.id.welfare11);
		welfare11.setOnClickListener(new PlusOnClickListener() {

			@Override
			protected void doIt() {

				goToWebpage("11");

			}
		});
		Button welfare12 = (Button) mActivity.findViewById(R.id.welfare12);
		welfare12.setOnClickListener(new PlusOnClickListener() {

			@Override
			protected void doIt() {

				goToWebpage("12");

			}
		});
		Button welfare13 = (Button) mActivity.findViewById(R.id.welfare13);
		welfare13.setOnClickListener(new PlusOnClickListener() {

			@Override
			protected void doIt() {

				goToWebpage("13");

			}
		});
	}

	/**
	 * 웹페이지로 이동
	 * 
	 */
	protected void goToWebpage(String no) {
		
		((MainActivity) mActivity).showWebpage(FWWebpageUrls.URL_MAIN2
				+ FWWebpageUrls.WELFARE_POSTFIX + no);
		
		
//		Intent intent = new Intent(mActivity, FWWebViewActivity.class);
//		intent.putExtra(PlusConstants.KEY_URL, FWWebpageUrls.URL_MAIN2
//				+ FWWebpageUrls.WELFARE_POSTFIX + no);
//		startActivity(intent);

	}

	@Override
	protected void notifyListView() {
		// TODO Auto-generated method stub
		
	}

}
