/*
 * ManipulatorManager.java
 *
 * Copyright (C) 2009-11 by RStudio, Inc.
 *
 * This program is licensed to you under the terms of version 3 of the
 * GNU Affero General Public License. This program is distributed WITHOUT
 * ANY EXPRESS OR IMPLIED WARRANTY, INCLUDING THOSE OF NON-INFRINGEMENT,
 * MERCHANTABILITY OR FITNESS FOR A PARTICULAR PURPOSE. Please refer to the
 * AGPL (http://www.gnu.org/licenses/agpl-3.0.txt) for more details.
 *
 */
package org.rstudio.studio.client.workbench.views.plots.ui.manipulator;

import org.rstudio.core.client.widget.ProgressImage;
import org.rstudio.core.client.widget.ToolbarButton;
import org.rstudio.studio.client.workbench.commands.Commands;
import org.rstudio.studio.client.workbench.views.plots.model.Manipulator;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.PopupPanel.PositionCallback;

public class ManipulatorManager
{
   public ManipulatorManager(Panel plotsSurface,
                             Commands commands,
                             ManipulatorChangedHandler changedHandler)
   {
      ManipulatorResources resources = ManipulatorResources.INSTANCE;
      ManipulatorStyles styles = ManipulatorStyles.INSTANCE;
      
      // references
      plotsSurface_ = plotsSurface;
       
      // no manipulator to start
      manipulator_ = null;
      manipulatorPopup_ = null;
      
      // create manipulator button
      manipulatorButton_ = new ToolbarButton(
            resources.manipulateButton(),
            new ClickHandler() { 
               public void onClick(ClickEvent event)
               {
                  showManipulatorPopup();
               }
            });
      manipulatorButton_.addStyleName(styles.manipulateButton());
      manipulatorButton_.setTitle(commands.showManipulator().getTooltip());
      plotsSurface_.add(manipulatorButton_);
      manipulatorButton_.setVisible(false);
      manipulatorProgress_ = new ProgressImage(resources.manipulateProgress());
      manipulatorProgress_.addStyleName(styles.manipulateProgress());
      plotsSurface_.add(manipulatorProgress_);
      manipulatorProgress_.setVisible(false);
      
      // create manipulator popup panel
      manipulatorPopup_ = new ManipulatorPopupPanel(changedHandler);
   }
   
   
   public void setManipulator(Manipulator manipulator, boolean show)
   {
      if (isNewManipulatorState(manipulator))
      {
         // set active manipulator
         manipulator_ = manipulator;
             
         // set visibility of manipulator button
         manipulatorButton_.setVisible(manipulator_ != null);
         
         // update UI
         manipulatorPopup_.update(manipulator_); 
         
         // if we have a manipulator then show if requested, otherwise hide
         if (manipulator_ != null)
         {
            // show if requested
            if (show)
               showManipulatorPopup();  
         }
         else
         {
            manipulatorPopup_.hide();
         }
      }
   }
   
   public void showManipulator()
   {
      if (manipulator_ != null)
         showManipulatorPopup();
   }
   
   public void setProgress(boolean showProgress)
   {
      if (showProgress)
      {
         manipulatorButton_.setVisible(false);
         manipulatorProgress_.show(true);
      }
      else
      {
         manipulatorProgress_.show(false);
         manipulatorButton_.setVisible(manipulator_ != null);
      }
   }
   
   private boolean isNewManipulatorState(Manipulator manipulator)
   {
      if (manipulator_ == null && manipulator == null)
         return false;
      if (manipulator_ == null && manipulator != null)
         return true;
      else if (manipulator == null && manipulator_ != null)
         return true;
      else if (!manipulator_.getID().equals(manipulator.getID()))
         return true;
      else
         return false;
   }
   
   private void showManipulatorPopup()
   {
      // show it if necessary
      if (!manipulatorPopup_.isShowing())
      {
         manipulatorPopup_.recordPreviouslyFocusedElement();
         
         manipulatorPopup_.setPopupPositionAndShow(new PositionCallback(){
            @Override
            public void setPosition(int offsetWidth, int offsetHeight)
            {
               manipulatorPopup_.setPopupPosition(
                     plotsSurface_.getAbsoluteLeft() - offsetWidth + 20,
                     plotsSurface_.getAbsoluteTop() - 6);
               
               manipulatorPopup_.focusFirstControl();
            }
            
         }) ;
         
         
      }
   }
   
   
   private final Panel plotsSurface_;
   private Manipulator manipulator_;
   private ToolbarButton manipulatorButton_;
   private ProgressImage manipulatorProgress_;
   private ManipulatorPopupPanel manipulatorPopup_;
  
}
