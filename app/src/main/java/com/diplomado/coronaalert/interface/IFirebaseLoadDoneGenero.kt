package com.diplomado.coronaalert.`interface`

import com.diplomado.coronaalert.model.Genero

interface IFirebaseLoadDoneGenero {
    fun onFirebaseLoadSucessGenero(tipogeneroList:List<Genero>)
    fun onFirebaseLoadFailedGenero(message:String)
}