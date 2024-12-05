import {Comments} from "./Comments.tsx";
import React from "react";
import en from 'javascript-time-ago/locale/en'
import TimeAgo from "javascript-time-ago";

TimeAgo.addDefaultLocale(en);
const App: React.FC = () => (
    <div className="container mx-auto p-4">
        <Comments/>
    </div>
);

export default App
