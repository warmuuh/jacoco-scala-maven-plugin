/*******************************************************************************
 * Copyright (c) 2009, 2013 Mountainminds GmbH & Co. KG and Contributors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Evgeny Mandrikov - initial API and implementation
 *    Kyle Lieber - implementation of CheckMojo
 *
 *******************************************************************************/
package timezra.maven.jacoco.scala;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.apache.maven.project.MavenProject;
import org.codehaus.plexus.util.FileUtils;
import org.jacoco.core.analysis.Analyzer;
import org.jacoco.core.analysis.CoverageBuilder;
import org.jacoco.core.analysis.IBundleCoverage;
import org.jacoco.core.analysis.ICoverageVisitor;
import org.jacoco.core.data.ExecutionDataStore;
import org.jacoco.maven.FileFilter;

/**
 * Creates an IBundleCoverage.
 */
public class BundleCreator {

    private final MavenProject project;
    private final FileFilter fileFilter;

    /**
     * Construct a new BundleCreator given the MavenProject and FileFilter.
     * 
     * @param project
     *            the MavenProject
     * @param fileFilter
     *            the FileFilter
     */
    public BundleCreator(final MavenProject project, final FileFilter fileFilter) {
        this.project = project;
        this.fileFilter = fileFilter;
    }

    /**
     * 
     * Factory method for creating an Analyzer
     * 
     * @param executionDataStore
     * @param coverageVisitor
     * @return an Analyzer
     */
    protected Analyzer createAnalyzer(final ExecutionDataStore executionDataStore, final ICoverageVisitor coverageVisitor) {
        return new Analyzer(executionDataStore, coverageVisitor);
    }

    /**
     * Create an IBundleCoverage for the given ExecutionDataStore.
     * 
     * @param executionDataStore
     *            the execution data.
     * @return the coverage data.
     * @throws IOException
     *             if class files can't be read
     */
    public IBundleCoverage createBundle(final ExecutionDataStore executionDataStore) throws IOException {
        final CoverageBuilder builder = new CoverageBuilder();
        final Analyzer analyzer = createAnalyzer(executionDataStore, builder);
        final File classesDir = new File(project.getBuild().getOutputDirectory());

        @SuppressWarnings("unchecked")
        final List<File> filesToAnalyze = FileUtils.getFiles(classesDir, fileFilter.getIncludes(), fileFilter.getExcludes());

        for (final File file : filesToAnalyze) {
            analyzer.analyzeAll(file);
        }

        return builder.getBundle(project.getName());
    }
}
