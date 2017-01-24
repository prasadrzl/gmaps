package gmaps.com.gmaps.presenters;

import java.util.ArrayList;

import javax.inject.Inject;

import gmaps.com.gmaps.models.GitHubRepos;
import gmaps.com.gmaps.network.WebApiService;
import gmaps.com.gmaps.views.HomeView;
import rx.Observable;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by Durga Prasad on 17-01-2017.
 */

public class HomePresenter extends BasePresenterImp<HomeView> {

    private WebApiService webApiService;
    private HomeView homeView;
    private Subscription subscription;

    @Inject
    public HomePresenter(WebApiService service) {
        this.webApiService = service;
    }

    public void getReposData() {
        homeView = getView();
        if (homeView != null) {
            homeView.showProgress();
            getDataFromGithub();
        }
    }

    private void getDataFromGithub() {
        Observable<ArrayList<GitHubRepos>> gitHubRepos = webApiService.getGitRepos("shivamdev31");
        subscription = gitHubRepos.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<ArrayList<GitHubRepos>>() {
                    @Override
                    public void onCompleted() {
                        homeView.hideProgress();
                    }

                    @Override
                    public void onError(Throwable e) {
                        homeView.hideProgress();
                        homeView.showError(e);

                    }

                    @Override
                    public void onNext(ArrayList<GitHubRepos> gitHubReposes) {
                        homeView.showContent(gitHubReposes);
                    }
                });
    }

    @Override
    public void detachView() {
        if (subscription != null && !subscription.isUnsubscribed()) {
            subscription.unsubscribe();
        }
        super.detachView();
    }
}
