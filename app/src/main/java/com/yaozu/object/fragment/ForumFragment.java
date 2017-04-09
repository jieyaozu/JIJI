package com.yaozu.object.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.yaozu.object.R;
import com.yaozu.object.bean.SectionBean;
import com.yaozu.object.db.dao.SectionDao;
import com.yaozu.object.entity.SectionReqData;
import com.yaozu.object.httpmanager.RequestManager;
import com.yaozu.object.utils.DataInterface;
import com.yaozu.object.widget.PagerSlidingTabStrip;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by jxj42 on 2017/4/3.
 */

public class ForumFragment extends BaseFragment {
    public static String TAG = "ForumFragment";
    private ViewPager viewPager;
    private PagerSlidingTabStrip pagerSlidingTabStrip;
    private ForumPagerAdapter forumPagerAdapter;

    private SectionDao sectionDao;
    private List<SectionBean> beanList;
    private SectionBean sectionBean = new SectionBean();

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewPager = (ViewPager) view.findViewById(R.id.forum_viewpager);
        pagerSlidingTabStrip = (PagerSlidingTabStrip) view.findViewById(R.id.forum_sliding_tab);
        sectionDao = new SectionDao(this.getActivity());
        beanList = sectionDao.findSelectSections();
        //第一页固定为热贴
        sectionBean.setSectionid("9999");
        sectionBean.setSectionname("热贴");
        if (beanList == null || beanList.size() <= 0) {
            beanList.add(sectionBean);
            getScetionListData();
        } else {
            beanList.add(0, sectionBean);
            forumPagerAdapter = new ForumPagerAdapter(getFragmentManager());
            viewPager.setAdapter(forumPagerAdapter);
            pagerSlidingTabStrip.setViewPager(viewPager);
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_forum_home, container, false);
        return view;
    }

    /**
     * 向服务器获取所有的版块,前7个默认为选中的
     */
    private void getScetionListData() {
        String url = DataInterface.FIND_ALL_SECTION;
        RequestManager.getInstance().getRequest(this.getActivity(), url, SectionReqData.class, new RequestManager.OnResponseListener() {
            @Override
            public void onSuccess(Object object, int code, String message) {
                if (object != null) {
                    SectionReqData sectionReqData = (SectionReqData) object;
                    List<SectionReqData.Section> sectionList = sectionReqData.getBody().getSections();
                    for (int i = 0; i < sectionList.size(); i++) {
                        SectionBean bean = new SectionBean();
                        bean.setSectionid(sectionList.get(i).getSection_id());
                        bean.setSectionname(sectionList.get(i).getSection_name());
                        if (i <= 6) {
                            bean.setSelect("true");
                            beanList.add(bean);
                        } else {
                            bean.setSelect("false");
                        }
                        sectionDao.add(bean);
                    }
                    forumPagerAdapter = new ForumPagerAdapter(getFragmentManager());
                    viewPager.setAdapter(forumPagerAdapter);
                    pagerSlidingTabStrip.setViewPager(viewPager);
                }
            }

            @Override
            public void onFailure(int code, String message) {

            }
        });
    }

    /**
     * 更新数据库中的数据(因为模块名称有可能会变,为了保持和服务器一致需要适时更新一下)
     *
     * @param sectionList 服务器上所有的版块
     */
    private void updateDbdata(List<SectionReqData.Section> sectionList) {
        List<SectionBean> beanList = new ArrayList<>();
        for (int i = 0; i < sectionList.size(); i++) {
            SectionBean bean = new SectionBean();
            bean.setSectionid(sectionList.get(i).getSection_id());
            bean.setSectionname(sectionList.get(i).getSection_name());
            beanList.add(bean);
        }

        for (SectionBean sectionBean : beanList) {
            boolean isHave = sectionDao.isHaveRecord(sectionBean.getSectionid());
            if (!isHave) {
                sectionDao.add(sectionBean);
            } else {
                sectionDao.updateName(sectionBean);
            }
        }
    }

    private class ForumPagerAdapter extends FragmentPagerAdapter {
        public Map<Integer, Fragment> fragments = new HashMap<Integer, Fragment>();

        public ForumPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            Fragment fragment = fragments.get(position);
            if (fragment == null) {
                fragment = new ForumChildFragment();
                fragments.put(position, fragment);
            }
            return fragment;
        }

        @Override
        public int getCount() {
            return beanList.size();
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return beanList.get(position).getSectionname();
        }
    }
}
