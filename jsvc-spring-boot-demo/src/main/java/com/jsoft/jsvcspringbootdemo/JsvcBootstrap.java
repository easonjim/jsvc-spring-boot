package com.jsoft.jsvcspringbootdemo;

import org.springframework.boot.loader.JarLauncher;
import org.springframework.boot.loader.jar.JarFile;

/**
 * Bootstrap for jsvc
 *
 * @author jim
 * @date 2018/06/29
 */
public class JsvcBootstrap extends JarLauncher{

    private static ClassLoader classLoader = null;
    private static JsvcBootstrap bootstrap = null;
    
    private static String[] args=null;

    protected void launch(String[] args, String mainClass, ClassLoader classLoader, boolean wait)
            throws Exception {
        Thread.currentThread().setContextClassLoader(classLoader);
        Thread thread = new Thread(() -> {
            try {
                createMainMethodRunner(mainClass, args, classLoader).run();
            } catch (Exception ex) {
            }
        });
        thread.setContextClassLoader(classLoader);
        thread.setName(Thread.currentThread().getName());
        thread.start();
        if (wait == true) {
            thread.join();
        }
    }
    
    public static void init(String[] arguments){
        args = arguments;
    }

    public static void start() {
        bootstrap = new JsvcBootstrap();
        try {
            JarFile.registerUrlProtocolHandler();
            classLoader = bootstrap.createClassLoader(bootstrap.getClassPathArchives());
            bootstrap.launch(args, bootstrap.getMainClass(), classLoader, true);
        } catch (Exception ex) {
            ex.printStackTrace();
            System.exit(1);
        }
    }

    public static void stop() {
        try {
            if (bootstrap != null) {
                bootstrap.launch(null, bootstrap.getMainClass(), classLoader, true);
                bootstrap = null;
                classLoader = null;
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            System.exit(1);
        }
    }

    public static void destroy(){
        
    }

    public static void main(String[] args) {
        String mode = args != null && args.length > 0 ? args[0] : null;
        if ("start".equals(mode)) {
            JsvcBootstrap.start();
        } else if ("stop".equals(mode)) {
            JsvcBootstrap.stop();
        }
    }
    
}
