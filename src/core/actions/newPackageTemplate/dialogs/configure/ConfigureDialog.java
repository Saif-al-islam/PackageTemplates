package core.actions.newPackageTemplate.dialogs.configure;

import base.BaseDialog;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.ValidationInfo;
import com.intellij.util.ui.GridBag;
import global.models.PackageTemplate;
import global.utils.factories.GridBagFactory;
import global.wrappers.PackageTemplateWrapper;
import net.miginfocom.layout.CC;
import net.miginfocom.layout.LC;
import net.miginfocom.swing.MigLayout;
import org.jetbrains.annotations.Nullable;

import java.awt.*;

/**
 * Created by CeH9 on 22.06.2016.
 */

public abstract class ConfigureDialog extends BaseDialog implements ConfigureView {

    private ConfigurePresenter presenter;

    public ConfigureDialog(Project project) {
        super(project);
        presenter = new ConfigurePresenterImpl(project, this);
    }

    public ConfigureDialog(Project project, PackageTemplate packageTemplate) {
        super(project);
        presenter = new ConfigurePresenterImpl(project, packageTemplate, this);
    }

    @Override
    public void buildView(PackageTemplateWrapper ptWrapper) {
        panel.setLayout(new MigLayout(new LC().fillX()));
        panel.add(ptWrapper.buildView(), new CC().pushX().growX().wrap());
    }


    //=================================================================
    //  Dialog specific stuff
    //=================================================================
    public abstract void onSuccess(PackageTemplate packageTemplate);
    public abstract void onFail();

    @Override
    public void preShow() {
        presenter.onPreShow();
    }

    @Nullable
    @Override
    protected ValidationInfo doValidate() {
        return presenter.doValidate();
    }

    @Override
    public void onOKAction() {
        presenter.onOKAction();
    }

    @Override
    public void onCancelAction() {
        presenter.onFail();
    }
}
