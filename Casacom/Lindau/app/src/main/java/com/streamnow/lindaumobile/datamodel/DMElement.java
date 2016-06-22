package com.streamnow.lindaumobile.datamodel;

import com.streamnow.lindaumobile.interfaces.IMenuPrintable;

/** !
 * Created by Miguel Estévez on 12/2/16.
 */
public abstract class DMElement implements IMenuPrintable
{
    public enum DMElementType //Este objeto puede tomar 2 posibles valores.
    {
        DMElementTypeCategory,
        DMElementTypeDocument
    }

    public DMElementType elementType; //inicializamos el objeto
}
