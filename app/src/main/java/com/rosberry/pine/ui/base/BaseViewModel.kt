package com.rosberry.pine.ui.base

import androidx.lifecycle.ViewModel
import com.github.terrakok.cicerone.Router
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

abstract class BaseViewModel(protected val router: Router) : ViewModel()