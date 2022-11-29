/*
 * Copyright 2010-2021 Australian Signals Directorate
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package au.gov.asd.tac.constellation.views.find.gui;

import au.gov.asd.tac.constellation.utilities.text.SeparatorConstants;
import javax.swing.DefaultComboBoxModel;

/**
 *
 * @author betelgeuse
 */
public class StringListPanel extends javax.swing.JPanel {

    private static final String COMMA = "Comma";
    private static final String TAB = "Tab";
    private static final String SEMI_COLON = "Semi-colon";
    private static final String STRING = "String";
    private String delimiter = COMMA;

    /**
     * Creates a new StringListPanel with the given content.
     * <p>
     * The content is comma-delimited: it will be transformed to multi-line
     * ('\n' delimited) for the text area.
     *
     * @param content the content.
     */
    public StringListPanel(final String content) {
        initComponents();

        // Ensure the default form visibility is set:
        lblStringDelimiter.setVisible(false);
        txtStringDelimiter.setVisible(false);

        txtContent.setText(content.replace(SeparatorConstants.COMMA, SeparatorConstants.NEWLINE));
        txtContent.setCaretPosition(content.length());
    }

    /**
     * Return the multi-line content of the text area with newlines replaced by
     * ",".
     *
     * @return the multi-line content of the text area with newlines replaced by
     * ",".
     */
    public String getContent() {
        return txtContent.getText().replace(SeparatorConstants.NEWLINE, SeparatorConstants.COMMA);
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jScrollPane1 = new javax.swing.JScrollPane();
        txtContent = new javax.swing.JTextArea();
        cmbDelimiter = new javax.swing.JComboBox<>();
        lblDelimiter = new javax.swing.JLabel();
        lblStringDelimiter = new javax.swing.JLabel();
        txtStringDelimiter = new javax.swing.JTextField();
        jLabel1 = new javax.swing.JLabel();
        splitLinesButton = new javax.swing.JButton();

        txtContent.setColumns(20);
        txtContent.setRows(5);
        jScrollPane1.setViewportView(txtContent);

        cmbDelimiter.setModel(new DefaultComboBoxModel<String>(new String[]{COMMA, TAB, SEMI_COLON, STRING}));
        cmbDelimiter.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cmbDelimiterActionPerformed(evt);
            }
        });

        org.openide.awt.Mnemonics.setLocalizedText(lblDelimiter, org.openide.util.NbBundle.getMessage(StringListPanel.class, "StringListPanel.lblDelimiter.text")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(lblStringDelimiter, org.openide.util.NbBundle.getMessage(StringListPanel.class, "StringListPanel.lblStringDelimiter.text")); // NOI18N

        txtStringDelimiter.setText(org.openide.util.NbBundle.getMessage(StringListPanel.class, "StringListPanel.txtStringDelimiter.text")); // NOI18N
        txtStringDelimiter.setToolTipText(org.openide.util.NbBundle.getMessage(StringListPanel.class, "StringListPanel.txtStringDelimiter.toolTipText")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(jLabel1, org.openide.util.NbBundle.getMessage(StringListPanel.class, "StringListPanel.jLabel1.text")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(splitLinesButton, org.openide.util.NbBundle.getMessage(StringListPanel.class, "StringListPanel.splitLinesButton.text")); // NOI18N
        splitLinesButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                splitLinesButtonActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane1)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel1)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(lblDelimiter)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(cmbDelimiter, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(lblStringDelimiter)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(txtStringDelimiter, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addComponent(splitLinesButton)))
                        .addGap(0, 180, Short.MAX_VALUE)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 258, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(cmbDelimiter, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblDelimiter)
                    .addComponent(lblStringDelimiter)
                    .addComponent(txtStringDelimiter, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(splitLinesButton))
                .addContainerGap())
        );
    }// </editor-fold>//GEN-END:initComponents

    private void cmbDelimiterActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmbDelimiterActionPerformed

        updateDelimiter();
    }//GEN-LAST:event_cmbDelimiterActionPerformed

    private void splitLinesButtonActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_splitLinesButtonActionPerformed
    {//GEN-HEADEREND:event_splitLinesButtonActionPerformed
        final String delim;
        switch (delimiter) {
            case COMMA:
                delim = SeparatorConstants.COMMA;
                break;
            case TAB:
                delim = SeparatorConstants.TAB;
                break;
            case SEMI_COLON:
                delim = SeparatorConstants.SEMICOLON;
                break;
            default:
                delim = txtStringDelimiter.getText();
                break;
        }

        final String[] lines = txtContent.getText().split(delim);
        final StringBuilder b = new StringBuilder();
        for (final String line : lines) {
            if (b.length() > 0) {
                b.append(SeparatorConstants.NEWLINE);
            }

            b.append(line);
        }

        txtContent.setText(b.toString());
    }//GEN-LAST:event_splitLinesButtonActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JComboBox<String> cmbDelimiter;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JLabel lblDelimiter;
    private javax.swing.JLabel lblStringDelimiter;
    private javax.swing.JButton splitLinesButton;
    private javax.swing.JTextArea txtContent;
    private javax.swing.JTextField txtStringDelimiter;
    // End of variables declaration//GEN-END:variables

    private void updateDelimiter() {
        final String currentItem = (String) cmbDelimiter.getSelectedItem();

        if (currentItem.equals(STRING)) {
            lblStringDelimiter.setVisible(true);
            txtStringDelimiter.setVisible(true);
        } else {
            lblStringDelimiter.setVisible(false);
            txtStringDelimiter.setVisible(false);
        }

        delimiter = currentItem;
    }
}
