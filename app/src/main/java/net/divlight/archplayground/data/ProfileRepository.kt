package net.divlight.archplayground.data

import io.reactivex.Completable
import io.reactivex.Single
import io.reactivex.schedulers.Schedulers
import java.util.concurrent.TimeUnit

interface ProfileRepository {
    companion object {
        fun getInstance(): ProfileRepository = StubProfileRepository
    }

    fun fetchProfile(): Single<Profile>
    fun updateProfile(profile: Profile): Completable

    private object StubProfileRepository : ProfileRepository {
        private var profile: Profile? = null

        override fun fetchProfile(): Single<Profile> = if (profile != null) {
            Single.just(profile)
        } else {
            // ネットワークからの取得を再現するためにdelayを噛ませる
            Single.just(Profile())
                .delay(1500, TimeUnit.MILLISECONDS)
                .doOnSuccess { profile = it }
                .subscribeOn(Schedulers.io())
        }


        override fun updateProfile(profile: Profile): Completable = Completable.complete()
            .delay(1500, TimeUnit.MILLISECONDS)
            .doOnComplete { this.profile = profile }
    }
}
