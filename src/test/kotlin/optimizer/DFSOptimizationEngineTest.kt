package optimizer

import com.github.sebastianp265.graph.StateGraph
import com.github.sebastianp265.graph.Transition
import com.github.sebastianp265.optimizer.DFSOptimizationEngine
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

class DFSOptimizationEngineTest : FunSpec({

    test("optimize finds best state with single actions") {
        data class TestState(val depth: Int, val value: Int) : Comparable<TestState> {
            override fun compareTo(other: TestState): Int = value.compareTo(other.value)
        }

        data class Decision(val name: String)

        class TestStateGraph : StateGraph<TestState, Decision> {
            override fun possibleTransitions(state: TestState): List<Transition<TestState, Decision>> {
                if (state.depth >= 3) return emptyList()
                return listOf(
                    Transition(listOf(Decision("a")), TestState(state.depth + 1, state.value + 10)),
                    Transition(listOf(Decision("b")), TestState(state.depth + 1, state.value + 5))
                )
            }
        }

        val (bestState, transitions) = DFSOptimizationEngine.optimize(TestStateGraph(), TestState(0, 0))

        bestState.value shouldBe 30
        transitions.size shouldBe 3
        transitions.forEach { it[0].name shouldBe "a" }
    }

    test("optimize returns initial state when terminal") {
        data class TestState(val value: Int) : Comparable<TestState> {
            override fun compareTo(other: TestState): Int = value.compareTo(other.value)
        }

        data class Decision(val name: String)

        class TerminalGraph : StateGraph<TestState, Decision> {
            override fun possibleTransitions(state: TestState) = emptyList<Transition<TestState, Decision>>()
        }

        val (bestState, transitions) = DFSOptimizationEngine.optimize(TerminalGraph(), TestState(42))

        bestState.value shouldBe 42
        transitions shouldBe emptyList()
    }

    test("explore all paths in dfs order") {
        data class TestState(val path: String, val value: Int) : Comparable<TestState> {
            override fun compareTo(other: TestState): Int = value.compareTo(other.value)
        }

        data class Decision(val name: String)

        val visitedTerminalStates = mutableListOf<String>()

        class DFSTrackingGraph : StateGraph<TestState, Decision> {
            override fun possibleTransitions(state: TestState): List<Transition<TestState, Decision>> {
                if (state.path.length >= 4) {
                    visitedTerminalStates.add(state.path)
                    return emptyList()
                }
                return listOf(
                    Transition(
                        listOf(Decision("L")),
                        TestState(state.path + "L", state.value + 1)
                    ),
                    Transition(
                        listOf(Decision("R")),
                        TestState(state.path + "R", state.value + 1)
                    )
                )
            }
        }

        DFSOptimizationEngine.optimize(DFSTrackingGraph(), TestState("", 0))

        // DFS visits all 2^4 = 16 terminal states from a binary tree of depth 4
        // Order: LLLL, LLLR, LLRL, LLRR, LRLL, ... RRRR
        visitedTerminalStates.size shouldBe 16
        visitedTerminalStates[0] shouldBe "LLLL"
        visitedTerminalStates[1] shouldBe "LLLR"
        visitedTerminalStates.last() shouldBe "RRRR"
    }

})
