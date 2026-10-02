package com.spas.ui;

import javax.swing.*;

import com.spas.ui.panels.StudentManagementPanel;
import com.spas.ui.panels.SubjectManagementPanel;
import com.spas.ui.panels.MarksManagementPanel;
import com.spas.ui.panels.AttendanceManagementPanel;
import com.spas.ui.panels.RankingPanel;
import com.spas.ui.panels.TrendAnalyticsPanel;
import com.spas.ui.panels.RiskEnginePanel;
import com.spas.ui.panels.HomeDashboardPanel;
import com.spas.ui.panels.ReportsPanel;
import com.spas.ui.panels.ClassIntelligencePanel;
import com.spas.ui.panels.SystemSettingsPanel;

public class AdminDashboard extends BaseDashboard {

    public AdminDashboard() {
        super("Admin Dashboard");
        // Show default card
        if (contentPanel.getComponentCount() > 0) {
            cardLayout.show(contentPanel, "Home");
        }
    }

    @Override
    protected void setupSidebar() {
        addSidebarItem("Home", new HomeDashboardPanel());
        addSidebarItem("Student Management", new StudentManagementPanel());
        addSidebarItem("Subject Management", new SubjectManagementPanel());
        addSidebarItem("Marks Entry", new MarksManagementPanel());
        addSidebarItem("Attendance Entry", new AttendanceManagementPanel());
        addSidebarItem("Ranking Engine", new RankingPanel());
        addSidebarItem("Reports", new ReportsPanel());
        addSidebarItem("Trend Analytics", new TrendAnalyticsPanel());
        addSidebarItem("Risk Engine", new RiskEnginePanel());
        addSidebarItem("Class Intelligence", new ClassIntelligencePanel());
        addSidebarItem("System Settings", new SystemSettingsPanel());
    }
}
