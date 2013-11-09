package epfl.sweng.services;

import epfl.sweng.events.EventEmitter;
import epfl.sweng.events.EventListener;
import epfl.sweng.proxy.Proxy;
import epfl.sweng.ui.QuestionActivity;

public abstract class QuestionActivityService extends EventEmitter implements
		Service, EventListener {

	private QuestionActivity mActivity;
	
	public QuestionActivityService(QuestionActivity activity) {
		addListener(activity);
		mActivity = activity;
		Proxy.getInstance().addListener(this);
	}
	
	protected QuestionActivity getActivity() {
		return mActivity;
	}
	
	protected void setActivity(QuestionActivity activity) {
		mActivity = activity;
		addListener(activity);
	}
}
