package fr.xebia.extras.selma.it.utils;

import fr.xebia.extras.selma.codegen.MapperProcessor;
import org.junit.Assert;

import javax.tools.*;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Created with IntelliJ IDEA.
 * User: slemesle
 * Date: 22/11/2013
 * Time: 12:59
 * To change this template use File | Settings | File Templates.
 */
public class TestCompiler {


    private final static TestCompiler INSTANCE = new TestCompiler();
    private final String SRC_DIR;
    private final String OUT_DIR;
    private final String GEN_DIR;
    private final String TARGET_DIR;
    private final  List<File> classPath;
    private final Map<String, TestCompilerEnv> environments;


    private TestCompiler(){

        String basePath = System.getProperty("user.dir");

        SRC_DIR = basePath + "/src/test/java";
        OUT_DIR = basePath + "/target/it/classes";
        GEN_DIR = basePath + "/target/it/generated-sources/mapping";
        TARGET_DIR = basePath + "/target";

        classPath = new ArrayList<>();

        classPath.addAll(findJars());
        environments = new HashMap<String, TestCompilerEnv>();

    }

    public static TestCompiler getInstance() {
        return INSTANCE;
    }



    public TestCompilerEnv compileFor(Class<? extends IntegrationTestBase> aClass) throws Exception{
        TestCompilerEnv res;


        if (environments.containsKey(aClass.getCanonicalName())){
            return environments.get(aClass.getCanonicalName());
        }

        res = new TestCompilerEnv();
        res.init(aClass);
        environments.put(aClass.getCanonicalName(),res);
        return res;
    }






    private final Collection<? extends File> findJars() {
        List<File> res = new ArrayList<>();
        Path p = Paths.get(TARGET_DIR);
        try (DirectoryStream<Path> stream = Files.newDirectoryStream(p, "*.jar")){

            for (Path path : stream) {
                res.add(path.toFile());
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
        return res;
    }


    private void processCompileAnnotation(Class<?> aClass, List<File> classes) {
        List<Compile> compiles = new ArrayList<>();
        Compile compile = aClass.getAnnotation(Compile.class);
        if (compile != null){
            compiles.add(compile);
        }
        compile = aClass.getSuperclass().getAnnotation(Compile.class);
        if (compile != null){
            compiles.add(compile);
        }

        for (Compile res : compiles) {
            String[] packages = res.withPackage();
            for (int i = 0; i < packages.length; i++) {
                String aPackage = packages[i];
                classes.addAll( listFilesIn(new File(String.format("%s/%s", SRC_DIR, aPackage.replace('.', '/')))));
            }

            Class<?>[] classesTab = res.withClasses();
            for (int i = 0; i < classesTab.length; i++) {
                Class<?> zeClass = classesTab[i];
                classes.add((new File(String.format("%s/%s.java", SRC_DIR, zeClass.getCanonicalName().replace('.', '/')))));
            }
        }

    }

    private Collection<? extends File> listFilesIn(File file) {
        Collection<File> res = new ArrayList<>();

        if ( file.exists() ) {
            File[] files = file.listFiles();
            for ( int i = 0; i < files.length; i++ ) {
                if ( files[i].isDirectory() ) {
                    res.addAll(listFilesIn(files[i]));
                }
                else {
                    res.add(files[i]);
                }
            }
        }
        return res;
    }

    private boolean compile(DiagnosticCollector<JavaFileObject> diagnostics, List<File> classes){
        JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
//        List<String> processorOptions = getProcessorOptions( testMethod );

        StandardJavaFileManager fileManager = compiler.getStandardFileManager(null, null, null);

        Iterable<? extends JavaFileObject> compilationUnits = fileManager.getJavaFileObjectsFromFiles( classes );

        try {
            fileManager.setLocation( StandardLocation.CLASS_PATH, classPath );
            fileManager.setLocation( StandardLocation.CLASS_OUTPUT, Arrays.asList(new File(OUT_DIR)) );
            fileManager.setLocation( StandardLocation.SOURCE_OUTPUT, Arrays.asList( new File( GEN_DIR ) ) );
        }
        catch ( IOException e ) {
            throw new RuntimeException( e );
        }

        JavaCompiler.CompilationTask task = compiler.getTask(
                null,
                fileManager,
                diagnostics,
                /*processorOptions*/null,
                null,
                compilationUnits
        );
        task.setProcessors( Arrays.asList( new MapperProcessor()) );


        boolean res  =  task.call();

        for (Diagnostic<? extends JavaFileObject > diagnostic : diagnostics.getDiagnostics()) {
            System.out.println(diagnostic.toString());
        }
        return res;
    }

    private List<File> getSourceFiles(Class<?>  ... classes) {
        List<File> liste = new ArrayList<>();
        for (Class<?> aClass : classes) {
            liste.add(new File(String.format("%s/%s.java", SRC_DIR, aClass.getCanonicalName().replace('.', '/'))));
        }
        return liste;
    }


    private  void createOutputDirs() {
        File directory = new File( OUT_DIR );
        deleteDirectory( directory );
        directory.mkdirs();

        directory = new File( GEN_DIR );
        deleteDirectory( directory );
        directory.mkdirs();
    }

    private  void deleteDirectory(File path) {
        if ( path.exists() ) {
            File[] files = path.listFiles();
            for ( int i = 0; i < files.length; i++ ) {
                if ( files[i].isDirectory() ) {
                    deleteDirectory( files[i] );
                }
                else {
                    files[i].delete();
                }
            }
        }
        path.delete();
    }


    class TestCompilerEnv {
        DiagnosticCollector<JavaFileObject> diagnostics;
        boolean compilationResult;
        List<File> classes;
        final AtomicBoolean initialized = new AtomicBoolean(false);
        boolean shouldFail;
        Class<?> aClass;


        private synchronized void init(Class<?> aClass)throws Exception {
            if (!initialized.get()){
                this.aClass = aClass;

                classes = new ArrayList<>();

                processCompileAnnotation(aClass, classes);

                createOutputDirs();

                Thread.currentThread().setContextClassLoader(
                        new URLClassLoader(
                                new URL[]{new File(OUT_DIR).toURI().toURL()},
                                Thread.currentThread().getContextClassLoader()
                        )
                );

                diagnostics = new DiagnosticCollector<JavaFileObject>();

                compilationResult = compile(diagnostics, classes);

                Compile compile = aClass.getAnnotation(Compile.class);
                shouldFail = compile.shouldFail();
                initialized.set(true);
            }
        }


        protected boolean compilationSuccess() {
            return compilationResult;
        }

        protected void assertCompilation(){
            Assert.assertTrue(String.format( "Compilation of class %s result should be %s", aClass.getSimpleName(), !shouldFail), !shouldFail == compilationResult );
        }

        public DiagnosticCollector<JavaFileObject> diagnostics() {

            return diagnostics;
        }
    }
}