            ---
            name: livedata-observable
            title: livedata-observable
            description: livedata-observable
            ---

            ---

name: livedata-observable
description: LiveData, Observable patterns for old UI architecture
metadata:
author: android-team
version: "1.0"
---

# LiveData & Observable Expert

## LiveData:

- MutableLiveData для изменяемых данных
- LiveData для неизменяемых (observe)
- Transformations (map, switchMap)
- MediatorLiveData для комбинации источников

## Observation:

- observe(this) с lifecycle awareness
- observeForever (осторожно, утечки!)
- SingleLiveEvent для one-time events
- DoubleLiveData для избежания duplicate events

## DataBinding:

- @Bindable аннотации
- notifyPropertyChanged(BR.property)
- Two-way binding (@={variable})
- Binding adapters (@BindingAdapter)

## RxJava (если используется):

- Observable, Flowable, Single, Maybe, Completable
- Schedulers (io, computation, main)
- dispose в onDestroy/onDestroyView
- RxLifecycle или AutoDispose
