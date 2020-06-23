package com.diplomado.coronaalert.`interface`

import com.diplomado.coronaalert.model.Genero

interface IFirebaseLoadDoneGenero {
    fun onFirebaseLoadSucess(tipogeneroList:List<Genero>)
    fun onFirebaseLoadFailed(message:String)
}