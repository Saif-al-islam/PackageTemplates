package core.actions.newPackageTemplate.dialogs.implement;

import base.BaseDialog;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.ValidationInfo;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiDirectory;
import com.intellij.util.ui.GridBag;
import global.models.PackageTemplate;
import global.utils.*;
import global.wrappers.GlobalVariableWrapper;
import global.wrappers.PackageTemplateWrapper;

import java.awt.*;

/**
 * Created by CeH9 on 14.06.2016.
 */
public abstract class ImplementDialog extends BaseDialog implements ImplementView {

    public abstract void onSuccess(PackageTemplateWrapper ptWrapper);
    public abstract void onCancel();

    private IFaceImplementPresenter presenter;


    public ImplementDialog(Project project, String title, PackageTemplate packageTemplate, VirtualFile virtualFile) {
        super(project);
        init();
        setTitle(title);
        presenter = new ImplementPresenter(this, packageTemplate, virtualFile, project);
    }

    @Override
    protected ValidationInfo doValidate() {
        return presenter.doValidate();
    }

    @Override
    public void preShow() {
        panel.setLayout(new GridBagLayout());
        presenter.onPreShow();
    }

    @Override
    public void buildView(PackageTemplateWrapper ptWrapper) {
        GridBag gridBag = GridBagFactory.getBagForConfigureDialog();
        panel.add(ptWrapper.buildView(), gridBag.nextLine().next());
    }

    @Override
    public void onOKAction() {
        presenter.onOKAction();
    }

    @Override
    public void onCancelAction() {
        presenter.onCancelAction();
    }
}
