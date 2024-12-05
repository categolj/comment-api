import React, {useEffect, useState} from "react";
import {Button} from "./retro-ui/Button.tsx";
import CommentTable from "./CommentTable.tsx";
import {Text} from "./retro-ui/Text.tsx";
import {Avatar} from "./retro-ui/Avatar.tsx";
import {Menu} from "./retro-ui/Menu.tsx";

interface GoogleUser {
    id: string,
    name: string,
    email: string,
    picture: string
}

export const Comments: React.FC = () => {
    const [user, setUser] = useState<GoogleUser | null>(null);
    const [csrfToken, setCsrfToken] = useState<string>('');

    useEffect(() => {
        const fetchUser = async () => {
            try {
                const response = await fetch('/admin/whoami', {method: 'GET'});
                if (response.ok) {
                    const data = await response.json();
                    setUser(data || null);
                    setCsrfToken(data.csrfToken);
                } else {
                    setUser(null);
                }
            } catch (error) {
                console.error('Error fetching user data:', error);
                setUser(null);
            }
        };
        fetchUser();
    }, []);
    return <div>
        {user ? (
            <>
                <div className="flex justify-between items-center p-4 bg-gray-50 mb-4">
                    <Text as="h3">Comments</Text>
                    <div className="flex items-center justify-center">
                        <Avatar className={"mr-2"}>
                            <Avatar.Image src={`${user.picture}`}
                                          alt={`${user.name}`}/>
                            <Avatar.Fallback>{`${user.name}`}</Avatar.Fallback>
                        </Avatar>
                        {user.email && (
                            <Menu>
                                <Menu.Trigger asChild>
                                    <Button>Logged in as: <span className="font-semibold">{user.email}</span></Button>
                                </Menu.Trigger>
                                <Menu.Content className="min-w-36">
                                    <Menu.Item onClick={() => {
                                        window.location.href = '/logout';
                                    }}>Logout</Menu.Item>
                                </Menu.Content>
                            </Menu>
                        )}
                    </div>
                </div>
                <CommentTable csrfToken={csrfToken}/>
            </>
        ) : (
            <Button onClick={() => {
                window.location.href = `/admin/login?redirect_path=${location.pathname}`;
            }}>
                Log in with Google
            </Button>
        )}
    </div>;
};