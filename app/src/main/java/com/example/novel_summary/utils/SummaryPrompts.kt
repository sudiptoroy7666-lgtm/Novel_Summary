package com.example.novel_summary.utils

object SummaryPrompts {

    fun getShortSummaryPrompt(content: String): String {
        return """
You are a careful summarizer of light novels and web novels. Summarize this chapter in 3-5 bullet points.
Focus ONLY on key events and major plot developments.

Rules:
- Use past tense, third-person
- Each bullet: 1-2 short sentences
- Keep original names, terms, and honorifics as they appear
- Do NOT invent details not in the text

Content:
 $content

Summary (3-5 bullet points):
        """.trimIndent()
    }

    fun getDetailedSummaryPrompt(content: String): String {
        return """
Create a detailed narrative summary of this light novel / web novel chapter that captures the reading experience.

Include:
• Key events in chronological order with scene transitions
• Character actions, decisions, and emotional reactions
• Important dialogue (paraphrased or quoted when crucial)
• Plot developments and revelations
• Character interactions and relationship dynamics

Style:
- Past tense, third person (unless original is first-person, then keep first-person)
- Maintain the story's tone and pacing
- Length: 2-4 substantial paragraphs

Special Care for Light Novels/Web Novels:
- Preserve original names, terms, and honorifics (e.g., -san, -kun, -sama, -sensei, -niisan)
- Keep skill names, technique names, and special terms unchanged (e.g., "Shadow Bind", "Qi Condensation")
- Preserve cultivation/magic system terminology
- Maintain the distinctive "light novel" narrative voice

Fidelity: Do NOT invent scenes, characters, or dialogue not present in the text.

Content:
 $content

Detailed Summary:
        """.trimIndent()
    }

    fun getVeryDetailedSummaryPrompt(content: String): String {
        return """
Create a comprehensive, immersive summary of this light novel / web novel chapter.
Someone reading your summary should feel like they've experienced the chapter firsthand.

CRITICAL REQUIREMENTS:

📖 NARRATIVE STRUCTURE:
• Follow the chapter's scene-by-scene progression
• Include scene transitions and setting changes
• Maintain the story's pacing (don't rush action, don't drag slow moments)

👥 CHARACTERS:
• Character thoughts, internal monologue, and reactions
• Emotional states and how they change
• Character decisions and their motivations
• Physical actions and body language

💬 DIALOGUE & INTERACTION:
• Important dialogue (quote or closely paraphrase key lines from the text)
• Character voice and speaking style when relevant
• Conversations that reveal plot, character, or conflict

🎭 DETAILS & ATMOSPHERE:
• Specific names (characters, places, techniques, items) — keep original spelling
• Worldbuilding elements introduced or referenced
• Combat/action sequences with blow-by-blow description
• Sensory details (what characters see, hear, feel)
• Tension, suspense, or mystery elements

🔮 PLOT & DEVELOPMENT:
• Major and minor plot points in order
• Revelations, discoveries, or new information
• Foreshadowing or hints about future events
• Cliffhangers or unresolved questions

🎌 LIGHT NOVEL / WEB NOVEL SPECIFICS (IMPORTANT):
• HONORIFICS: Preserve all honorifics exactly as written (-san, -kun, -sama, -sensei, -dono, -chan, -niisan, -oneesan, etc.)
• NAMES: Keep character names, place names, and item names exactly as they appear (don't anglicize or translate)
• SKILLS/TECHNIQUES: Preserve skill names, technique names, spell names unchanged (e.g., "Dragon Breath", "Sword Qi", "Nine Yang Sword")
• CULTIVATION TERMS: Keep cultivation terminology intact (e.g., Qi Condensation, Foundation Establishment, Golden Core, Nascent Soul, Realm, Level)
• GAME/RPG TERMS: If present, keep game terminology (Level, Stats, Skills, HP, MP, Dungeon, Guild, etc.)
• TITLE/STATUS: Preserve any title references (Young Master, Elder, Sect Leader, Demon King, Hero, etc.)
• CULTURAL TERMS: Keep cultural references as-is (e.g., tatami, futon, kimono, sect, clan, etc.)
• SOUND EFFECTS: Preserve distinctive sound effects in dialogue where relevant (e.g., "Hmph!", "Tch!", "Ugh!")

STYLE GUIDE:
• Past tense, third person narrative (or first-person if that's the original POV)
• Maintain the original tone (serious, humorous, dark, comedic, etc.)
• Show, don't just tell
• Use vivid verbs and specific nouns
• Preserve dramatic moments and emotional peaks
• Keep the distinctive "light novel" writing flavor — often includes internal monologue, dramatic reactions, and expressive dialogue

⚠️ FIDELITY RULES (CRITICAL):
• DO NOT invent scenes, characters, or dialogue not in the text
• DO NOT add motivations or thoughts not clearly implied by the text
• DO NOT translate or change names, terms, or honorifics
• If something is ambiguous, use cautious phrasing ("seemed", "appeared", "suggested")
• Quote dialogue verbatim only if it appears in the source; otherwise paraphrase

LENGTH: 6-10 detailed paragraphs organized by scene/section.

Content:
 $content

Very Detailed Chapter Summary:
        """.trimIndent()
    }

    fun getSummaryTypeDescription(type: String): String {
        return when (type) {
            "short" -> "Short Summary (Key events only)"
            "detailed" -> "Detailed Summary (Full narrative)"
            "very_detailed" -> "Very Detailed Summary (Complete reading experience)"
            else -> "Summary"
        }
    }
}