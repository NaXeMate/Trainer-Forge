import { useNavigate } from "react-router";

import heroLogo from "../../assets/brand/logo/logo_extended_no_background.png";
import pokedexIcon from "../../assets/brand/sections/pokédex_icon.png";
import simulatorIcon from "../../assets/brand/sections/simulator_icon.png";
import teamsIcon from "../../assets/brand/sections/teams_icon.png";
import trainerIcon from "../../assets/brand/sections/trainer_icon.png";
import SectionCard from "../../components/cards/sectionCard";
import Footer from "../../components/layout/footer";
import Header from "../../components/layout/header";

import "./home.css";

const sections = [
  {
    title: "PokeDex",
    description:
      "The greatest Pokemon enciclopedia that reunites all its available knowledge",
    image: pokedexIcon,
    imageAlt: "PokéDex",
    href: "/pokedex",
  },
  {
    title: "Teams",
    description:
      "Build your dream-teams with every detail and precise data for excellent viability results",
    image: teamsIcon,
    imageAlt: "Teams",
    href: "/teams",
  },
  {
    title: "Simulator",
    description:
      "Try your teams in simulated battles and take note of your victory chances",
    image: simulatorIcon,
    imageAlt: "Simulator",
    href: "/simulator",
  },
  {
    title: "Trainer Card",
    description:
      "Create your own TrainerForge profile. See your stats, achievements and much more",
    image: trainerIcon,
    imageAlt: "Trainer Card",
    href: "/trainer",
  },
] as const;

export default function Home() {
  const navigate = useNavigate();

  return (
    <div className="home-page">
      <Header />

      <main className="home-page__main">
        <section className="home-page__hero" aria-labelledby="home-title">
          <h1 id="home-title" className="visually-hidden">
            TrainerForge
          </h1>
          <img
            className="home-page__hero-image"
            src={heroLogo}
            alt="TrainerForge"
          />
        </section>

        <section
          className="home-page__sections"
          aria-label="TrainerForge sections">
          {sections.map(({ href, ...section }) => (
            <SectionCard
              key={href}
              {...section}
              className="home-page__section-card"
              onAccess={() => navigate(href)}
            />
          ))}
        </section>
      </main>

      <Footer />
    </div>
  );
}
