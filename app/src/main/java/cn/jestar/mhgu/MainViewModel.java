package cn.jestar.mhgu;

import android.arch.core.util.Function;
import android.arch.lifecycle.LifecycleOwner;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.Transformations;
import android.arch.lifecycle.ViewModel;

import java.util.List;
import java.util.Set;

import cn.jestar.db.IndexDao;
import cn.jestar.db.MyDataBase;
import cn.jestar.db.bean.IndexBean;
import cn.jestar.mhgu.version.VersionLiveData;

import static android.app.Activity.RESULT_OK;


/**
 * Created by 花京院 on 2019/1/17.
 */

public class MainViewModel extends ViewModel {

    private IndexDao mDao;
    private LiveData<List<IndexBean>> mTyps;
    private LiveData<List<IndexBean>> mSelects;
    private MutableLiveData<String> mSelectTag = new MutableLiveData<>();
    private MutableLiveData<String> mMenuSelect = new MutableLiveData<>();
    private MutableLiveData<Integer> mSelectType = new MutableLiveData<>();
    private MutableLiveData<Integer> mSelectParent = new MutableLiveData<>();
    private VersionLiveData mVersionData = new VersionLiveData();
    private int mType;

    public MainViewModel() {
        mDao = MyDataBase.getInstace().getDao();
        mTyps = Transformations.switchMap(mSelectType, new Function<Integer, LiveData<List<IndexBean>>>() {
            @Override
            public LiveData<List<IndexBean>> apply(Integer input) {
                return mDao.queryByType(mType);
            }
        });
        mSelects = Transformations.switchMap(mSelectParent, new Function<Integer, LiveData<List<IndexBean>>>() {
            @Override
            public LiveData<List<IndexBean>> apply(Integer input) {
                return mDao.queryTypeWithParent(mType, input);
            }
        });
    }

    public void onTypeSelect(int type) {
        mType = type;
        mSelectType.setValue(type);
    }

    public void onTypeParentSelect(int parent) {
        mSelectParent.setValue(parent);
    }

    public void observerType(LifecycleOwner lifecycle, Observer<List<IndexBean>> observer) {
        mTyps.observe(lifecycle, observer);
    }

    public void observerParent(LifecycleOwner owner, Observer<List<IndexBean>> observer) {
        mSelects.observe(owner, observer);
    }

    public void observerMenuSelect(LifecycleOwner owner, Observer<String> observer) {
        mMenuSelect.observe(owner, observer);
    }

    public void observerTagSelect(LifecycleOwner owner, Observer<String> observer) {
        mSelectTag.observe(owner, observer);
    }

    public VersionLiveData getVersion() {
        mVersionData.getVersion();
        return mVersionData;
    }

    public void onTagSelect(String url) {
        mSelectTag.setValue(url);
    }

    public void onMenuSelect(String title) {
        mMenuSelect.setValue(title);
    }

    public int getIndex(Set<Integer> set) {
        if (set.isEmpty())
            return RESULT_OK;
        return (int) set.toArray()[0];
    }

}
