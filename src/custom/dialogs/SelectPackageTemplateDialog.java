package custom.dialogs;

import com.intellij.icons.AllIcons;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.openapi.ui.ValidationInfo;
import com.intellij.ui.ListCellRendererWrapper;
import com.intellij.ui.components.JBList;
import com.intellij.util.ui.GridBag;
import custom.impl.ReleaseListener;
import reborn.wrappers.PackageTemplateWrapper;
import state.SaveUtil;
import state.TemplateList;
import models.MyListModel;
import models.PackageTemplate;
import org.jetbrains.annotations.NotNull;
import utils.GridBagFactory;
import utils.TemplateValidator;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;


/**
 * Created by CeH9 on 24.06.2016.
 */
public abstract class SelectPackageTemplateDialog extends DialogWrapper {

    private JBList jbList;
    private Project project;
    private TemplateList templateList;

    public abstract void onSuccess(PackageTemplate packageTemplate);

    public abstract void onCancel();

    public SelectPackageTemplateDialog(Project project) {
        super(project);
        this.project = project;
        init();
        setTitle("Select File Template");
    }

    @Override
    public void show() {
        super.show();

        switch (getExitCode()) {
            case NewPackageDialog.OK_EXIT_CODE:
                onSuccess((PackageTemplate) jbList.getSelectedValue());
                break;
            case NewPackageDialog.CANCEL_EXIT_CODE:
                onCancel();
                break;
        }
    }

    @Override
    protected ValidationInfo doValidate() {
        if (jbList.isSelectionEmpty()) {
            return new ValidationInfo("Select item!", jbList);
        }

        ValidationInfo validationInfo = TemplateValidator.isTemplateValid((PackageTemplate) jbList.getSelectedValue());
        if (validationInfo != null) {
            return new ValidationInfo(validationInfo.message, jbList);
        }

        return null;
    }

    @Override
    protected JComponent createCenterPanel() {
        JSplitPane root = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
        root.setMinimumSize(new Dimension(400, 300));
        root.setEnabled(false);


        templateList = SaveUtil.getInstance().getTemplateList();
        jbList = new JBList();
        initJBList();

        root.add(getControls());
        root.add(jbList);

        return root;
    }

    private Component getControls() {
        JPanel container = new JPanel(new GridBagLayout());
        GridBag bag = GridBagFactory.getBagForSelectDialog();

        JButton jbAdd = getIconButton(AllIcons.General.Add);
        JButton jbDelete = getIconButton(AllIcons.General.Remove);
        JButton jbEdit = getIconButton(AllIcons.Modules.Edit);

        jbAdd.addMouseListener(new ReleaseListener() {
            @Override
            public void mouseReleased(MouseEvent event) {
                onAddAction();
            }
        });
        jbDelete.addMouseListener(new ReleaseListener() {
            @Override
            public void mouseReleased(MouseEvent event) {
                if (SwingUtilities.isLeftMouseButton(event)) {
                    onDeleteAction(jbDelete);
                }
            }
        });
        jbEdit.addMouseListener(new ReleaseListener() {
            @Override
            public void mouseReleased(MouseEvent event) {
                if (SwingUtilities.isLeftMouseButton(event)) {
                    onEditAction();
                }
            }
        });

        container.add(jbAdd, bag.nextLine().next());
        container.add(jbDelete, bag.next());
        container.add(jbEdit, bag.next());

        return container;
    }

    private void onEditAction() {
        ConfigurePackageTemplatesDialog dialog = new ConfigurePackageTemplatesDialog(project, ((PackageTemplate) jbList.getSelectedValue())) {
            @Override
            public void onSuccess(PackageTemplateWrapper packageTemplate) {
                SaveUtil.getInstance().save();
                System.out.println("onSuccess");
            }

            @Override
            public void onFail() {
                System.out.println("onFail");
            }
        };
        dialog.show();
    }

    private void onDeleteAction(JButton jbDelete) {
        int confirmDialog = JOptionPane.showConfirmDialog(jbDelete, "Delete Template");
        if (confirmDialog == JOptionPane.OK_OPTION) {
            if (jbList.getSelectedIndex() != -1) {
                templateList.remove(jbList.getSelectedValue());
                SaveUtil.getInstance().save();
                initJBList();
            }
        }
    }

    private void onAddAction() {
        ConfigurePackageTemplatesDialog dialog = new ConfigurePackageTemplatesDialog(project) {
            @Override
            public void onSuccess(PackageTemplateWrapper ptWrapper) {
                templateList.add(ptWrapper.getPackageTemplate());
                SaveUtil.getInstance().save();
                initJBList();
            }

            @Override
            public void onFail() {
                System.out.println("onFail");
            }
        };
        dialog.show();
    }

    @NotNull
    private JButton getIconButton(Icon icon) {
        JButton jButton = new JButton(icon);
        jButton.setBorder(BorderFactory.createEmptyBorder());
        return jButton;
    }

    private void initJBList() {
        jbList.removeAll();
        jbList.setModel(new MyListModel<>(templateList));

        jbList.setCellRenderer(new ListCellRendererWrapper<PackageTemplate>() {
            @Override
            public void customize(JList list, PackageTemplate template, int index, boolean selected, boolean hasFocus) {
                if (template != null) {
                    setText(template.getName());
                }
            }
        });
    }

}
