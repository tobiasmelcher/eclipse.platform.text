/*******************************************************************************
 * Copyright (c) 2000, 2004 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.search2.internal.ui.text;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.IWorkspaceRunnable;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.search.internal.ui.SearchPlugin;
import org.eclipse.search.ui.SearchUI;
import org.eclipse.search.ui.text.Match;

public class MarkerHighlighter extends Highlighter {
	private IFile fFile;
	private Map fMatchesToAnnotations;
	
	public MarkerHighlighter(IFile file) {
		fFile= file;
		fMatchesToAnnotations= new HashMap();
	}

	public void addHighlights(final Match[] matches) {
		try {
			SearchPlugin.getWorkspace().run(new IWorkspaceRunnable() {
				public void run(IProgressMonitor monitor) throws CoreException {
					for (int i = 0; i < matches.length; i++) {
						IMarker marker;
						marker = createMarker(matches[i]);
						fMatchesToAnnotations.put(matches[i], marker);
					}
				}
			}, fFile, IWorkspace.AVOID_UPDATE, null);
		} catch (CoreException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private IMarker createMarker(Match match) throws CoreException {
		IMarker marker= fFile.createMarker(SearchUI.SEARCH_MARKER);
		HashMap attributes= new HashMap(4);
		attributes.put(SearchUI.LINE, "this is the line attribute");
		attributes.put(IMarker.CHAR_START, new Integer(match.getOffset()));
		attributes.put(IMarker.CHAR_END, new Integer(match.getOffset()+match.getLength()));
		attributes.put(IMarker.LINE_NUMBER, new Integer(20));
		marker.setAttributes(attributes);
		return marker;
	}
	
	public void removeHighlights(Match[] matches) {
		for (int i= 0; i < matches.length; i++) {
			IMarker marker= (IMarker) fMatchesToAnnotations.remove(matches[i]);
			if (marker != null) {
				try {
					marker.delete();
				} catch (CoreException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}

	public  void removeAll() {
		try {
			fFile.deleteMarkers(SearchUI.SEARCH_MARKER, true, IResource.DEPTH_INFINITE);
		} catch (CoreException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
