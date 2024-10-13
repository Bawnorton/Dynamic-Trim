import org.gradle.api.Project
import org.gradle.api.tasks.TaskContainer

class LoaderData(private val project: Project, private val name: String) {
    private val isFabric = name == "fabric"
    private val isNeoForge = name == "neoforge"

    fun getVersion() : String = if (isNeoForge) {
        project.property("neoforge_loader").toString()
    } else {
        project.property("fabric_loader").toString()
    }

    override fun toString(): String {
        return name
    }

    fun neoforge(container: () -> TaskContainer) {
        if(isNeoForge) container.invoke()
    }

    fun fabric(container: () -> TaskContainer) {
        if(isFabric) container.invoke()
    }
}