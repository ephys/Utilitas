package be.ephys.utilitas.base.feature;

import be.ephys.utilitas.Utilitas;
import net.minecraftforge.fml.common.ModContainer;
import net.minecraftforge.fml.common.discovery.ASMDataTable;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.toposort.TopologicalSort;
import net.minecraftforge.fml.common.toposort.TopologicalSort.DirectedGraph;

import java.lang.reflect.Field;
import java.util.Collections;
import java.util.List;
import java.util.Set;

public class FeatureLoader {

    private final DirectedGraph<Class<? extends Feature>> dependencyGraph;
    private final List<Class<? extends Feature>> sortedFeatureClasses;

    public FeatureLoader(FMLPreInitializationEvent event) {
        this.dependencyGraph = findFeatureClasses(event);

        List<Class<? extends Feature>> sortedFeatures = TopologicalSort.topologicalSort(dependencyGraph);
        this.sortedFeatureClasses = Collections.unmodifiableList(sortedFeatures);
    }

    /**
     * Returns the list of features owned by the mod which generated the event.
     * The features are sorted in dependency order.
     *
     * @return The list of features.
     */
    public List<Class<? extends Feature>> getFeatureClasses() {
        return sortedFeatureClasses;
    }

    public Set<Class<? extends Feature>> getDependants(Class<? extends Feature> featureClass) {
        return this.dependencyGraph.edgesFrom(featureClass);
    }

    /**
     * Finds the list of features and puts them in a dependency graph.
     *
     * @param event The FML Init event.
     * @return The list of features.
     */
    private static DirectedGraph<Class<? extends Feature>> findFeatureClasses(FMLPreInitializationEvent event) {
        Utilitas.getLogger().info("Loading features from mod " + event.getModMetadata().modId + "(" + event.getModMetadata().name + ")");
        Set<ASMDataTable.ASMData> asm = event.getAsmData().getAll(FeatureMeta.class.getCanonicalName());

        DirectedGraph<Class<? extends Feature>> dependencyGraph = new DirectedGraph<>();

        for (ASMDataTable.ASMData asmData : asm) {
            // this fails if there is more than one mod in the same jar.
            if (!isFromMod(asmData, event.getModMetadata().modId)) {
                continue;
            }

            try {
                Class<?> aClass = Class.forName(asmData.getClassName());

                if (!Feature.class.isAssignableFrom(aClass)) {
                    throw new RuntimeException(aClass.getCanonicalName() + ": @FeatureMeta annotated classes must extend " + Feature.class.getCanonicalName());
                }

                // noinspection unchecked
                Class<? extends Feature> featureClass = (Class<? extends Feature>) aClass;
                FeatureMeta metadata = featureClass.getAnnotation(FeatureMeta.class);

                dependencyGraph.addNode(featureClass);
                for (Class<? extends Feature> dependencyClass : metadata.dependencies()) {
                    dependencyGraph.addNode(dependencyClass);
                    dependencyGraph.addEdge(dependencyClass, featureClass);
                }
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        }

        Utilitas.getLogger().info("Found " + dependencyGraph.size() + " features");

        return dependencyGraph;
    }

    private static boolean isFromMod(ASMDataTable.ASMData asmData, String modId) {
        List<ModContainer> containedMods = asmData.getCandidate().getContainedMods();

        for (ModContainer mod : containedMods) {
            if (mod.getModId().equals(modId)) {
                return true;
            }
        }

        return false;
    }

    public static Field getInstanceField(Class<? extends Feature> featureClass) {
        for (Field field : featureClass.getDeclaredFields()) {
            if (field.isAnnotationPresent(FeatureInstance.class)) {
                return field;
            }
        }

        return null;
    }
}
