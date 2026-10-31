/**
 * Datatypes que viajan por los Web Services del Servidor Central.
 * JAXB los serializa por sus campos (no hacen falta setters) y convierte LocalDate a texto yyyy-MM-dd.
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlJavaTypeAdapter(value = LocalDateAdapter.class, type = LocalDate.class)
package edu.edext.datatypes;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import java.time.LocalDate;
