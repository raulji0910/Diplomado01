package com.diplomado.coronaalert.`interface`

import com.diplomado.coronaalert.model.TipoSangre

interface IFirebaseLoadDoneTipoSangre {
    fun onFirebaseLoadSucessTipoSangre(tipoSangreList:List<TipoSangre>)
    fun onFirebaseLoadFailedTipoSangre(message:String)
}