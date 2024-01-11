package di

import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.realtime.RealtimeChannel
import io.github.jan.supabase.realtime.channel
import kotlin.jvm.JvmInline

@JvmInline
value class MissionChannel(val channel: RealtimeChannel)

fun getMissionChannel(supabaseClient: SupabaseClient): MissionChannel {
    return MissionChannel(supabaseClient.channel("mission_changes"))
}