package edu.edext.tools;

import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.DefaultTableCellRenderer;

/**
 *
 * @author vdraco
 */
public class jTable {
    public  final int ALIGN_CENTER=SwingConstants.CENTER;
    public  final int ALIGN_RIGHT=SwingConstants.RIGHT;
    public  final int ALIGN_LEFT=SwingConstants.LEFT;
    private DefaultTableModel model;
    private final int rowCount;
    private boolean empty=true;
    private final JTable obj;

    
    public jTable(JTable obj){
        this.obj=obj;
        this.rowCount=obj.getRowCount();
    }

    public void sizeColumn(int column, int size){
        obj.getColumnModel().getColumn(column).setPreferredWidth(size);
    }
    
    public void sizeColumn(int column, int size, int Alignment){
        obj.getColumnModel().getColumn(column).setPreferredWidth(size);
        alignment(column, Alignment);
    }
    
    public void alignment(int column, int Alignment){
        obj.getColumnModel().getColumn(column).setCellRenderer(getAlignCell(Alignment));
    }
    
    private DefaultTableCellRenderer getAlignCell(int Alignment){
        DefaultTableCellRenderer tmp=new DefaultTableCellRenderer();
        tmp.setHorizontalAlignment(Alignment);
        return tmp;
    }
    
    public DefaultTableModel getModel(){
        return (DefaultTableModel) this.obj.getModel();
    }
    
    public void addRow(Object[] rowData){
        if(model==null) model=getModel();
        if(rowData!=null){
            if(empty){
                empty=false;
                clear(); }
            model.addRow(rowData); }
        else clear();
    }
    
    public void assignData(){
        if(model!=null){
            obj.setModel(model);
            if(model.getRowCount()<rowCount)
                model.setNumRows(rowCount); }
    }
    
    public void clear(){
        if(model==null) model=getModel();
        if(model.getRowCount()!=0) model.setRowCount(0);
        if(empty) model.setRowCount(rowCount);
    }
    
}