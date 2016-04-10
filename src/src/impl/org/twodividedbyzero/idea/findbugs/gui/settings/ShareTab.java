/*
 * Copyright 2016 Andre Pfeiler
 *
 * This file is part of FindBugs-IDEA.
 *
 * FindBugs-IDEA is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * FindBugs-IDEA is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with FindBugs-IDEA.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.twodividedbyzero.idea.findbugs.gui.settings;

import com.intellij.ide.highlighter.XmlFileType;
import com.intellij.openapi.Disposable;
import com.intellij.openapi.fileChooser.FileChooserDescriptor;
import com.intellij.openapi.fileChooser.FileChooserDescriptorFactory;
import com.intellij.openapi.fileChooser.FileChooserFactory;
import com.intellij.openapi.fileChooser.FileTextField;
import com.intellij.openapi.options.ConfigurationException;
import com.intellij.openapi.ui.LabeledComponent;
import com.intellij.openapi.ui.MessageType;
import com.intellij.openapi.ui.TextFieldWithBrowseButton;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.ui.HyperlinkLabel;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.twodividedbyzero.idea.findbugs.core.WorkspaceSettings;
import org.twodividedbyzero.idea.findbugs.gui.common.HAlignment;
import org.twodividedbyzero.idea.findbugs.gui.common.VAlignment;
import org.twodividedbyzero.idea.findbugs.gui.common.VerticalFlowLayout;
import org.twodividedbyzero.idea.findbugs.resources.ResourcesLoader;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.io.File;

final class ShareTab extends JPanel implements SettingsOwner<WorkspaceSettings>, Disposable {
	private JLabel description;
	private HyperlinkLabel link;
	private LabeledComponent<TextFieldWithBrowseButton> importPathLabel;

	ShareTab() {
		super(new VerticalFlowLayout(HAlignment.Left, VAlignment.Top, 0, 0, true, false));

		description = new JLabel("<html>" + ResourcesLoader.getString("share.description"));
		description.setBorder(BorderFactory.createEmptyBorder(5, 5, 0, 5));
		description.setIcon(MessageType.INFO.getDefaultIcon());

		final String url = ResourcesLoader.getString("share.url");
		link = new HyperlinkLabel(); // LATER: HotspotPainter (Search) does not work with HyperlinkLabel
		link.setHyperlinkText(url);
		link.setHyperlinkTarget(url);

		final JPanel linkPane = new JPanel(new FlowLayout(FlowLayout.LEFT, MessageType.INFO.getDefaultIcon().getIconWidth() + 5, 0));
		linkPane.add(link);

		final FileChooserDescriptor descriptor = FileChooserDescriptorFactory.createSingleFileDescriptor(XmlFileType.INSTANCE);
		final FileTextField field = FileChooserFactory.getInstance().createFileTextField(descriptor, this);
		final TextFieldWithBrowseButton importPath = new TextFieldWithBrowseButton(field.getField());
		importPath.addBrowseFolderListener(
				ResourcesLoader.getString("settings.choose.title"),
				ResourcesLoader.getString("settings.choose.description"),
				null,
				descriptor
		);
		importPathLabel = new LabeledComponent<TextFieldWithBrowseButton>();
		importPathLabel.setBorder(BorderFactory.createEmptyBorder(5, 0, 0, 0));
		importPathLabel.setComponent(importPath);
		importPathLabel.setLabelLocation(BorderLayout.WEST);
		importPathLabel.setText(ResourcesLoader.getString("share.file.title"));

		add(description);
		add(linkPane);
		add(importPathLabel);
	}

	@Nullable
	private String getImportFilePath() {
		String ret = importPathLabel.getComponent().getText();
		if (StringUtil.isEmptyOrSpaces(ret)) {
			return null;
		}
		return ret.trim();
	}

	@Override
	public void setEnabled(final boolean enabled) {
		super.setEnabled(enabled);
		description.setEnabled(enabled);
		link.setEnabled(enabled);
		importPathLabel.setEnabled(enabled);
	}

	@Override
	public boolean isModified(@NotNull final WorkspaceSettings settings) {
		return !StringUtil.equals(settings.importFilePath, getImportFilePath());
	}

	@Override
	public void apply(@NotNull final WorkspaceSettings settings) throws ConfigurationException {
		final String filePath = getImportFilePath();
		if (filePath != null) {
			final File file = new File(filePath);
			if (!file.exists()) {
				throw new ConfigurationException(ResourcesLoader.getString("error.path.exists", filePath));
			}
			if (!file.isFile()) {
				throw new ConfigurationException(ResourcesLoader.getString("error.path.type", filePath));
			}
			if (!file.canRead()) {
				throw new ConfigurationException(ResourcesLoader.getString("error.file.readable", filePath));
			}
		}
		settings.importFilePath = filePath;
	}

	@Override
	public void reset(@NotNull final WorkspaceSettings settings) {
		importPathLabel.getComponent().setText(settings.importFilePath);
	}

	@Override
	public void dispose() {
	}

	@NotNull
	static String getSearchPath() {
		return ResourcesLoader.getString("settings.share");
	}

	@NotNull
	static String[] getSearchResourceKey() {
		return new String[]{
				"share.description",
				"share.file.title",
				"share.url"
		};
	}
}
