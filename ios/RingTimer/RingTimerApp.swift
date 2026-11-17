import SwiftUI
import CoreText

@main
struct RingTimerApp: App {
    @StateObject private var settings: Settings
    @StateObject private var engine: TimerEngine

    init() {
        Self.registerFonts()
        let s = Settings()
        _settings = StateObject(wrappedValue: s)
        _engine = StateObject(wrappedValue: TimerEngine(settings: s, sound: SoundPlayer()))
    }

    var body: some Scene {
        WindowGroup {
            ContentView()
                .environmentObject(settings)
                .environmentObject(engine)
        }
    }

    /// Loads the bundled .ttf files so no Info.plist setup is needed.
    private static func registerFonts() {
        let urls = Bundle.main.urls(forResourcesWithExtension: "ttf", subdirectory: nil) ?? []
        for url in urls {
            CTFontManagerRegisterFontsForURL(url as CFURL, .process, nil)
        }
    }
}
