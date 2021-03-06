/*
 * This source code is part of the research paper
 * "Applying Model-View-Presenter Architecture to the Android Framework with App Prototype"
 *
 * Software Architecture and Management,
 * Herman-Hollerith-Zentrum, Reutlingen University
 *
 * Authors:  Dennis G. Geisse,
 *           Iven John,
 *           Daniel Joos,
 *           Sebastian Kotstein,
 *           Michael Wurster
 *
 * Year:     2015
 */

package inf.msc.yawapp.favourites;

import android.content.Context;

import javax.inject.Inject;

import inf.msc.yawapp.common.GenericObservable;
import inf.msc.yawapp.common.GenericObserver;
import inf.msc.yawapp.model.FavouritesStore;
import inf.msc.yawapp.model.FavouritesStoreOperation;
import inf.msc.yawapp.model.Location;
import inf.msc.yawapp.navigation.NavigationPresenter;
import inf.msc.yawapp.navigation.SubmitSearchInteractor;

public class FavouritesPresenterImpl implements FavouritesPresenter, GenericObserver<FavouritesStoreOperation> {

    @Inject
    FavouritesView view;

    @Inject
    FavouritesStore model;

    @Inject
    GenericObservable<FavouritesStoreOperation> favouritesStoreObservable;

    @Inject
    SubmitSearchInteractor submitSearchInteractor;

    @Inject
    NavigationPresenter navigationPresenter;

    @Override
    public void init() {
        favouritesStoreObservable.registerObserver(this);
    }

    @Override
    public void deinit() {
        favouritesStoreObservable.unregisterObserver(this);
    }

    @Override
    public void update() {
        view.clearView();
        view.addFavourites(model.getAll());
    }

    @Override
    public void submitSearch(Location location) {
        submitSearchInteractor.submitSearch(location);
    }

    @Override
    public void navigateToSearch(Context context) {
        navigationPresenter.navigateSearch(context);
    }

    @Override
    public void update(GenericObservable<FavouritesStoreOperation> observable, FavouritesStoreOperation argument) {
        update();
    }
}
