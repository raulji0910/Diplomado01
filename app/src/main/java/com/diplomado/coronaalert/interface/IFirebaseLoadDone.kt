package com.diplomado.coronaalert.`interface`

import com.diplomado.coronaalert.model.TipoIdentificacion

interface IFirebaseLoadDone {
    fun onFirebaseLoadSucess(tipoIdentificacionList:List<TipoIdentificacion>)

    fun onFirebaseLoadFailed(message:String)
}