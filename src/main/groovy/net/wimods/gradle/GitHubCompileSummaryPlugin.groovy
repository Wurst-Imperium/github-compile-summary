package net.wimods.gradle

import org.gradle.api.DefaultTask
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.Task
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.logging.StandardOutputListener
import org.gradle.api.tasks.Internal
import org.gradle.api.tasks.TaskAction
import org.gradle.api.tasks.compile.JavaCompile

public class GitHubCompileSummaryPlugin implements Plugin<Project> {
	@Override
	public void apply(Project project) {
		String summaryPath = System.getenv("GITHUB_STEP_SUMMARY")
		if(summaryPath == null)
			return

		def summaryTask = project.tasks.register("githubCompileSummary",
			GitHubCompileSummaryTask) {

			it.group = "verification"
			it.description = "Writes failed Java compiler output to the GitHub step summary."
			it.summaryFile.set(new File(summaryPath))
			it.outputs.upToDateWhen { false }
		}

		project.tasks.withType(JavaCompile).configureEach { JavaCompile compileTask ->
			compileTask.doFirst { Task task ->
				OutputCaptureManager.start((JavaCompile)task)
			}

			compileTask.doLast {
				OutputCaptureManager.stop()
			}

			compileTask.finalizedBy(summaryTask)
		}
	}
}

public abstract class GitHubCompileSummaryTask extends DefaultTask {
	@Internal
	public abstract RegularFileProperty getSummaryFile()

	@TaskAction
	public void run() {
		OutputCapture capture = OutputCaptureManager.stop()
		if(capture == null)
			return

		String output = capture.output.toString().trim()
		if(output.isEmpty())
			return

		summaryFile.get().asFile.withWriterAppend("UTF-8") { writer ->
			writer.println()
			writer.println("## Java compilation failed")
			writer.println()
			writer.println("Task: `${capture.task.path}`")
			writer.println()
			writer.println("<details open>")
			writer.println("<summary>Compiler output</summary>")
			writer.println()
			writer.println("```text")
			writer.println(output)
			writer.println("```")
			writer.println("</details>")
		}
	}
}

public class OutputCaptureManager {
	private static OutputCapture capture

	public static void start(JavaCompile task) {
		capture = new OutputCapture(task: task)
		task.logging.addStandardOutputListener(capture.listener)
		task.logging.addStandardErrorListener(capture.listener)
	}

	public static OutputCapture stop() {
		OutputCapture result = capture
		capture = null

		if(result != null) {
			result.task.logging.removeStandardOutputListener(result.listener)
			result.task.logging.removeStandardErrorListener(result.listener)
		}

		return result
	}
}

public class OutputCapture {
	public JavaCompile task
	public final StringBuilder output = new StringBuilder()
	public final StandardOutputListener listener = { CharSequence text ->
		output.append(text)
	} as StandardOutputListener
}
